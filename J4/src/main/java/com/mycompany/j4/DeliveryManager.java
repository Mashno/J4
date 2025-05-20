/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.j4;

/**
 *
 * @author Владислав
 */
import java.sql.*;
import java.time.LocalDate;

public class DeliveryManager {

    
    public static int addDelivery() {
        String sqlInsert = "INSERT INTO Delivery(delivery_date) VALUES(?) RETURNING id";
        int deliveryId = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                deliveryId = rs.getInt("id");
                
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при регистрации поставки:");
            e.printStackTrace();
        }
        return deliveryId;
    }

    
    public static void addDeliveryDetail(int deliveryId, String componentType, int componentId, int amount) {
        String sql = "INSERT INTO DeliveryDetails(delivery_id, component_type, component_id, amount) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deliveryId);
            pstmt.setString(2, componentType); // "wood" или "core"
            pstmt.setInt(3, componentId);
            pstmt.setInt(4, amount);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении деталей поставки:");
            e.printStackTrace();
        }
    }
}
