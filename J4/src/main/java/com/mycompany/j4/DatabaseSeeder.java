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

public class DatabaseSeeder {

    // Список 15 типов древесины
    private static final String[] WOOD_TYPES = {
        "Берёза", "Дуб", "Ольха", "Клен", "Сосна",
    "Ель", "Липа", "Осина", "Бук", "Ясень",
    "Ива", "Тополь", "Граб", "Вяз", "Лиственница"
    };

    // Список 15 типов сердцевин
    private static final String[] CORE_TYPES = {
        "Перо феникса", "Сердечная струна дракона", "Волос единорога", "Волос вилы", "Хвост жеребца смерти",
    "Ус кота-воришки", "Перо громптицы", "Чешуйка василиска", "Паутина акромантлы", "Рог графорна",
    "Перо авгурея", "Перо дрикала", "Щупальце муртлапа", "Перо джобберкнолла", "Перо снигга"
    };

    // Проверка, пустая ли таблица
    private static boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }

    // Заполнение таблицы начальными данными
    private static void seedTableWithDefaults(Connection conn, String tableName, String[] types) throws SQLException {
        String insertSql = "INSERT INTO " + tableName + "(type, amount) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (String type : types) {
                pstmt.setString(1, type);
                pstmt.setInt(2, 0); // Начальное количество
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("✅ Добавлены стандартные значения для таблицы " + tableName);
        }
    }

    // Основной метод для инициализации
    public static void seedDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (isTableEmpty(conn, "Wood")) {
                seedTableWithDefaults(conn, "Wood", WOOD_TYPES);
            } else {
                System.out.println("ℹ️ Таблица Wood уже содержит данные.");
            }

            if (isTableEmpty(conn, "Core")) {
                seedTableWithDefaults(conn, "Core", CORE_TYPES);
            } else {
                System.out.println("ℹ️ Таблица Core уже содержит данные.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при заполнении начальных данных:");
            e.printStackTrace();
        }
    }
}
