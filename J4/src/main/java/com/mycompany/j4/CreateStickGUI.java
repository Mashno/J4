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
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CreateStickGUI extends JDialog {

    private Connection connection;

    public CreateStickGUI(JFrame parent) {
        super(parent, "Создать волшебную палочку", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        connection = DatabaseConnection.getConnection();

        setupUI();
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Выбор древесины
        JLabel woodLabel = new JLabel("Выберите древесину:");
        JComboBox<ComponentInfo> woodComboBox = new JComboBox<>();
        loadComponents("Wood", woodComboBox);

        // Выбор сердцевины
        JLabel coreLabel = new JLabel("Выберите сердцевину:");
        JComboBox<ComponentInfo> coreComboBox = new JComboBox<>();
        loadComponents("Core", coreComboBox);

        // Ввод цены
        JLabel priceLabel = new JLabel("Цена палочки:");
        JTextField priceField = new JTextField();

        // Кнопка создания
        JButton createButton = new JButton("✨ Создать палочку");

        createButton.addActionListener(e -> {
            ComponentInfo selectedWood = (ComponentInfo) woodComboBox.getSelectedItem();
            ComponentInfo selectedCore = (ComponentInfo) coreComboBox.getSelectedItem();

            if (selectedWood == null || selectedCore == null) {
                JOptionPane.showMessageDialog(this, "❗ Пожалуйста, выберите оба компонента.");
                return;
            }

            String priceText = priceField.getText();
            double price;

            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Введите корректную цену (положительное число).");
                return;
            }

            try {
                connection.setAutoCommit(false);

                // Уменьшаем количество древесины
                updateComponentAmount(selectedWood.id, selectedWood.type, -1);
                // Уменьшаем количество сердцевины
                updateComponentAmount(selectedCore.id, selectedCore.type, -1);
                // Создаём палочку с указанной ценой
                createMagicStick(selectedWood.id, selectedCore.id, price);

                connection.commit();
                JOptionPane.showMessageDialog(this, "✨ Палочка успешно создана!");

                // Опционально: обновляем списки
                woodComboBox.removeAllItems();
                coreComboBox.removeAllItems();
                loadComponents("Wood", woodComboBox);
                loadComponents("Core", coreComboBox);
                //priceField.setText("150.0");

            } catch (SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {}
                JOptionPane.showMessageDialog(this, "❌ Ошибка при создании палочки.");
                ex.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {}
            }
        });

        // Добавляем элементы на форму
        panel.add(woodLabel);
        panel.add(woodComboBox);
        panel.add(coreLabel);
        panel.add(coreComboBox);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(new JLabel(""));
        panel.add(createButton);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Загружает доступные компоненты (amount >= 1)
    private void loadComponents(String table, JComboBox<ComponentInfo> comboBox) {
        String sql = "SELECT id, type FROM " + table + " WHERE amount >= 1 ORDER BY type";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("type");
                comboBox.addItem(new ComponentInfo(id, table, name));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Ошибка загрузки компонентов.");
            e.printStackTrace();
        }
    }

    // Обновляет количество компонента
    private void updateComponentAmount(int id, String table, int delta) throws SQLException {
        String sql = "UPDATE " + table + " SET amount = amount + ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, delta);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    // Создаёт новую палочку
    private void createMagicStick(int woodId, int coreId, double price) throws SQLException {
        String sql = "INSERT INTO MagicStick(wood_id, core_id, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, woodId);
            pstmt.setInt(2, coreId);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
        }
    }

    // Вспомогательный класс для хранения информации о компоненте
    private static class ComponentInfo {
        int id;
        String type; // Wood / Core
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
}
