/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.j4;

/**
 *
 * @author Владислав
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RestockGUI extends JDialog {

    private Connection connection;
    private List<ComponentItem> deliveryItems = new ArrayList<>();
    private DefaultTableModel tableModel;

    public RestockGUI(JFrame parent) {
        super(parent, "Пополнение склада", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        connection = DatabaseConnection.getConnection();

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Панель выбора компонента
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JComboBox<String> componentTypeBox = new JComboBox<>(new String[]{"Древесина", "Сердцевина"});
        JComboBox<ComponentInfo> componentComboBox = new JComboBox<>();
        JTextField amountField = new JTextField("1");
        JButton addButton = new JButton("Добавить компонент");

        // Загружаем данные при изменении типа
        componentTypeBox.addActionListener(e -> loadComponents((String) componentTypeBox.getSelectedItem(), componentComboBox));

        // Кнопка добавления
        addButton.addActionListener(e -> {
            ComponentInfo selected = (ComponentInfo) componentComboBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "❗ Пожалуйста, выберите компонент.");
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountField.getText());
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Введите корректное положительное число.");
                return;
            }

            deliveryItems.add(new ComponentItem(selected.id, selected.type, selected.name, amount));
            updateTable();
        });

        inputPanel.add(new JLabel("Тип компонента:"));
        inputPanel.add(componentTypeBox);
        inputPanel.add(new JLabel("Выберите компонент:"));
        inputPanel.add(componentComboBox);
        inputPanel.add(new JLabel("Количество:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);

        // Таблица выбранных компонентов
        String[] columnNames = {"ID", "Тип", "Название", "Количество"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);

        // Кнопка создания поставки
        JButton createDeliveryButton = new JButton("📦 Создать поставку");
        createDeliveryButton.addActionListener(e -> createDelivery());

        // Компоновка
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createDeliveryButton, BorderLayout.SOUTH);
    }

    private void loadComponents(String type, JComboBox<ComponentInfo> comboBox) {
        String tableName = type.equals("Древесина") ? "Wood" : "Core";
        String sql = "SELECT id, type, amount FROM " + tableName;

        comboBox.removeAllItems();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("type");
                int amount = rs.getInt("amount");
                comboBox.addItem(new ComponentInfo(id, tableName, name));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Ошибка загрузки данных.");
            e.printStackTrace();
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ComponentItem item : deliveryItems) {
            tableModel.addRow(new Object[]{item.id, item.type, item.name, item.amount});
        }
    }

    private void createDelivery() {
        if (deliveryItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❗ Список компонентов пуст.");
            return;
        }

        try {
            connection.setAutoCommit(false);

            // Добавляем поставку
            String insertDelivery = "INSERT INTO Delivery(delivery_date) VALUES (?) RETURNING id";
            int deliveryId;
            try (PreparedStatement pstmt = connection.prepareStatement(insertDelivery)) {
                pstmt.setDate(1, Date.valueOf(LocalDate.now()));
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                deliveryId = rs.getInt("id");
            }

            // Добавляем детали поставки
            String insertDetail = "INSERT INTO DeliveryDetails(delivery_id, component_type, component_id, amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertDetail)) {
                for (ComponentItem item : deliveryItems) {
                    pstmt.setInt(1, deliveryId);
                    pstmt.setString(2, item.type); // Wood/Core
                    pstmt.setInt(3, item.id);
                    pstmt.setInt(4, item.amount);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Обновляем остатки на складе
            String updateStockSql;
            for (ComponentItem item : deliveryItems) {
                if (item.type.equals("Wood")) {
                    updateStockSql = "UPDATE Wood SET amount = amount + ? WHERE id = ?";
                } else {
                    updateStockSql = "UPDATE Core SET amount = amount + ? WHERE id = ?";
                }
                try (PreparedStatement pstmt = connection.prepareStatement(updateStockSql)) {
                    pstmt.setInt(1, item.amount);
                    pstmt.setInt(2, item.id);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            JOptionPane.showMessageDialog(this, "✅ Поставка успешно создана!");
            deliveryItems.clear();
            updateTable();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "❌ Ошибка при создании поставки.");
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    // Вспомогательные классы

    private static class ComponentInfo {
        int id;
        String type; // Wood/Core
        String name;

        public ComponentInfo(int id, String type, String name) {
            this.id = id;
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class ComponentItem {
        int id;
        String type; // Wood/Core
        String name;
        int amount;

        public ComponentItem(int id, String type, String name, int amount) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.amount = amount;
        }
    }
}
