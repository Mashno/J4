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
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StickFactory {

    public static void createMagicStick(int coreId, int woodId, double price) {
        String sql = "INSERT INTO MagicStick(core_id, wood_id, price, status, buyer) VALUES(?, ?, ?, 'available', NULL)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, coreId);
            pstmt.setInt(2, woodId);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            System.out.println("✨ Создана новая палочка: core_id=" + coreId + ", wood_id=" + woodId);

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при создании палочки:");
            e.printStackTrace();
        }
    }
}
