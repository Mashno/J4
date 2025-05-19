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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RestockDialog extends JDialog {

    public RestockDialog(JFrame parent) {
        super(parent, "Пополнить склад", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JComboBox<String> componentTypeBox = new JComboBox<>(new String[]{"Древесина", "Сердцевина"});
        JTextField typeIdField = new JTextField(10);
        JTextField amountField = new JTextField(10);

        JButton submitButton = new JButton("Добавить");

        add(new JLabel("Тип компонента:"));
        add(componentTypeBox);
        add(new JLabel("ID компонента:"));
        add(typeIdField);
        add(new JLabel("Количество:"));
        add(amountField);
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = componentTypeBox.getSelectedItem().toString();
                try {
                    int id = Integer.parseInt(typeIdField.getText());
                    int amount = Integer.parseInt(amountField.getText());

                    if (type.equals("Древесина")) {
                        updateWoodStock(id, amount);
                    } else {
                        updateCoreStock(id, amount);
                    }

                    JOptionPane.showMessageDialog(RestockDialog.this, "✅ Склад успешно пополнен!");
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(RestockDialog.this, "❌ Неверный формат ввода!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void updateWoodStock(int id, int amount) {
        String sql = "UPDATE Wood SET amount = amount + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCoreStock(int id, int amount) {
        String sql = "UPDATE Core SET amount = amount + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
