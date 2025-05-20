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

public class ComponentManager {

   
    public static void addWood(String type, int amount) {
        String sql = "INSERT INTO Wood(type, amount) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setInt(2, amount);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении древесины:");
            e.printStackTrace();
        }
    }

   
    public static void addCore(String type, int amount) {
        String sql = "INSERT INTO Core(type, amount) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setInt(2, amount);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении сердцевины:");
            e.printStackTrace();
        }
    }
}
