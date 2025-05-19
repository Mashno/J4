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
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT m.id, w.type AS wood, c.type AS core, m.price " +
                    "FROM MagicStick m " +
                    "JOIN Wood w ON m.wood_id = w.id " +
                    "JOIN Core c ON m.core_id = c.id");

            textArea.append("✨ Список палочек:\n\n");
            while (rs.next()) {
                textArea.append("ID: " + rs.getInt("id") + "\n");
                textArea.append("Древесина: " + rs.getString("wood") + "\n");
                textArea.append("Сердцевина: " + rs.getString("core") + "\n");
                textArea.append("Цена: " + rs.getDouble("price") + "\n");
                textArea.append("------------------------\n");
            }

        } catch (SQLException e) {
            textArea.setText("❌ Ошибка при загрузке палочек.");
            e.printStackTrace();
        }

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}
