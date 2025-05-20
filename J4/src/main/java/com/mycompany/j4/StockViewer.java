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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StockViewer extends JFrame {

    public StockViewer() {
        setTitle("Остатки на складе");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Тип");
        tableModel.addColumn("Название");
        tableModel.addColumn("Количество");

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        
        loadStockData(tableModel);

        
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadStockData(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            
            ResultSet woodRs = stmt.executeQuery("SELECT type, amount FROM Wood");
            while (woodRs.next()) {
                model.addRow(new Object[]{
                        "Древесина",
                        woodRs.getString("type"),
                        woodRs.getInt("amount")
                });
            }

            
            ResultSet coreRs = stmt.executeQuery("SELECT type, amount FROM Core");
            while (coreRs.next()) {
                model.addRow(new Object[]{
                        "Сердцевина",
                        coreRs.getString("type"),
                        coreRs.getInt("amount")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Ошибка при загрузке данных со склада.");
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockViewer());
    }
}
