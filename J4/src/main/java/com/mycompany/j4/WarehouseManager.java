/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.j4;

/**
 *
 * @author Владислав
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WarehouseManager {

    // Отобразить состояние склада
    public static void showStock() {
        System.out.println("\n📦 Текущее состояние склада:");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Показываем древесину
            ResultSet woodRs = stmt.executeQuery("SELECT * FROM Wood");
            System.out.println("\n🪵 Древесина:");
            while (woodRs.next()) {
                System.out.println("ID: " + woodRs.getInt("id") +
                        ", Тип: " + woodRs.getString("type") +
                        ", Количество: " + woodRs.getInt("amount"));
            }

            // Показываем сердцевину
            ResultSet coreRs = stmt.executeQuery("SELECT * FROM Core");
            System.out.println("\n🦴 Сердцевина:");
            while (coreRs.next()) {
                System.out.println("ID: " + coreRs.getInt("id") +
                        ", Тип: " + coreRs.getString("type") +
                        ", Количество: " + coreRs.getInt("amount"));
            }

            // Показываем количество палочек на складе
            ResultSet stickRs = stmt.executeQuery("SELECT COUNT(*) AS total_sticks FROM MagicStick WHERE NOT EXISTS (SELECT 1 FROM Sale WHERE Sale.stick_id = MagicStick.id)");
            if (stickRs.next()) {
                System.out.println("\n✨ Общее количество палочек на складе: " + stickRs.getInt("total_sticks"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при получении данных со склада:");
            e.printStackTrace();
        }
    }
}
