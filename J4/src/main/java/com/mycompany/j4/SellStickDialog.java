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

public class SellStickDialog extends JDialog {

    public SellStickDialog(JFrame parent) {
        super(parent, "Продать палочку", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JTextField stickIdField = new JTextField(10);
        JButton sellButton = new JButton("Продать");

        add(new JLabel("Введите ID палочки:"));
        add(stickIdField);
        add(sellButton);

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int stickId = Integer.parseInt(stickIdField.getText());
                    if (sellMagicStick(stickId)) {
                        JOptionPane.showMessageDialog(SellStickDialog.this, "💰 Палочка успешно 'продана'!");
                    } else {
                        JOptionPane.showMessageDialog(SellStickDialog.this, "❌ Палочка не найдена или уже продана.", "Ошибка", JOptionPane.WARNING_MESSAGE);
                    }
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SellStickDialog.this, "❌ Неверный формат ID!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Метод для "продажи" палочки (можно расширить)
    private boolean sellMagicStick(int stickId) {
        String sql = "DELETE FROM MagicStick WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stickId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
