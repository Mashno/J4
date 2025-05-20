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

public class SellStickGUI extends JDialog {

    private Connection connection;

    public SellStickGUI(JFrame parent) {
        super(parent, "Продать палочку", true);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        connection = DatabaseConnection.getConnection();

        setupUI();
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel stickLabel = new JLabel("Доступные палочки:");
        JComboBox<StickInfo> stickComboBox = new JComboBox<>();
        loadAvailableSticks(stickComboBox);

        JLabel buyerLabel = new JLabel("Имя покупателя:");
        JTextField buyerField = new JTextField();

        JButton sellButton = new JButton("?Совершить покупку");

        sellButton.addActionListener(e -> {
            StickInfo selectedStick = (StickInfo) stickComboBox.getSelectedItem();
            String buyerName = buyerField.getText().trim();

            if (selectedStick == null) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, выберите палочку.");
                return;
            }

            if (buyerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя покупателя.");
                return;
            }

            try {
                connection.setAutoCommit(false);

                
                updateStickStatus(selectedStick.id, buyerName);

                connection.commit();
                JOptionPane.showMessageDialog(this, "?Палочка успешно продана!");
                dispose();

            } catch (SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {}
                JOptionPane.showMessageDialog(this, "Ошибка при продаже палочки.");
                ex.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {}
            }
        });

        panel.add(stickLabel);
        panel.add(stickComboBox);
        panel.add(buyerLabel);
        panel.add(buyerField);
        panel.add(new JLabel(""));
        panel.add(sellButton);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    
    private void loadAvailableSticks(JComboBox<StickInfo> comboBox) {
        String sql = """
            SELECT m.id, w.type AS wood, c.type AS core, m.price
            FROM MagicStick m
            JOIN Wood w ON m.wood_id = w.id
            JOIN Core c ON m.core_id = c.id
            WHERE m.status = 'available'
            ORDER BY m.id;
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String wood = rs.getString("wood");
                String core = rs.getString("core");
                double price = rs.getDouble("price");

                String displayName = String.format("ID: %d | Древесина: %s | Сердцевина: %s | Цена: %.2f",
                        id, wood, core, price);

                comboBox.addItem(new StickInfo(id, displayName));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки палочек.");
            e.printStackTrace();
        }
    }

    
    private void updateStickStatus(int stickId, String buyerName) throws SQLException {
        String sql = "UPDATE MagicStick SET status = 'sold', buyer = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, buyerName);
            pstmt.setInt(2, stickId);
            pstmt.executeUpdate();
        }
    }

    
    private static class StickInfo {
        int id;
        String display;

        public StickInfo(int id, String display) {
            this.id = id;
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
