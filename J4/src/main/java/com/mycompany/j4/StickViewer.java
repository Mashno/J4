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
import java.sql.*;

public class StickViewer extends JFrame {

    public StickViewer() {
        setTitle("Список волшебных палочек");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                SELECT m.id, w.type AS wood, c.type AS core, 
                       m.price, m.status, m.buyer
                FROM MagicStick m
                JOIN Wood w ON m.wood_id = w.id
                JOIN Core c ON m.core_id = c.id
                ORDER BY m.id;
            """;

            ResultSet rs = stmt.executeQuery(sql);

            textArea.append(" Список волшебных палочек:\n\n");

            while (rs.next()) {
                int id = rs.getInt("id");
                String wood = rs.getString("wood");
                String core = rs.getString("core");
                double price = rs.getDouble("price");
                String status = rs.getString("status");
                String buyer = rs.getObject("buyer", String.class);

                textArea.append(String.format("ID: %d%n", id));
                textArea.append(String.format("Древесина: %s%n", wood));
                textArea.append(String.format("Сердцевина: %s%n", core));
                textArea.append(String.format("Цена: %.2f%n", price));
                textArea.append(String.format("Статус: %s%n", status));
                textArea.append(String.format("Покупатель: %s%n", buyer == null ? "—" : buyer));
                textArea.append("------------------------\n");
            }

        } catch (SQLException e) {
            textArea.setText("Ошибка при загрузке палочек.");
            e.printStackTrace();
        }

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        setVisible(true);
    }
}
