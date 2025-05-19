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

public class DatabaseCleaner {

    public static void clearAllData() {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        // Удаление всех палочек
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM MagicStick")) {
            stmt.executeUpdate();
        }

        // Удаление деталей поставок и самих поставок
        try (PreparedStatement stmtDetails = conn.prepareStatement("DELETE FROM DeliveryDetails");
             PreparedStatement stmtDelivery = conn.prepareStatement("DELETE FROM Delivery")) {
            stmtDetails.executeUpdate();
            stmtDelivery.executeUpdate();
        }

        // Обнуление количества в Wood и Core
        try (PreparedStatement stmtWood = conn.prepareStatement("UPDATE Wood SET amount = 0");
             PreparedStatement stmtCore = conn.prepareStatement("UPDATE Core SET amount = 0")) {
            stmtWood.executeUpdate();
            stmtCore.executeUpdate();
        }

        // Сброс последовательностей
        resetSequence(conn, "MagicStick", "id");
        resetSequence(conn, "Delivery", "id");
        resetSequence(conn, "DeliveryDetails", "id");

        conn.commit();
        System.out.println("🧹 Все данные обновлены. ID начинаются заново с 1.");

    } catch (SQLException e) {
        System.err.println("❌ Ошибка при очистке базы данных:");
        e.printStackTrace();
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex2) {
            System.err.println("❌ Ошибка при откате транзакции:");
            ex2.printStackTrace();
        }
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при закрытии соединения:");
            e.printStackTrace();
        }
    }
}

    // Вспомогательный метод для сброса последовательности
    private static void resetSequence(Connection conn, String table, String column) throws SQLException {
        String seqName = getSequenceName(conn, table, column);
        if (seqName != null) {
            String sql = "ALTER SEQUENCE " + seqName + " RESTART WITH 1";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        }
    }

    // Получаем имя последовательности для SERIAL-столбца
    private static String getSequenceName(Connection conn, String table, String column) throws SQLException {
        String sql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, table);
            pstmt.setString(2, column);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return null;
    }
}