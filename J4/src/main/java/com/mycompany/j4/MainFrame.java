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

public class MainFrame extends JFrame {

    public MainFrame() {
        DatabaseSeeder.seedDatabase();
        setTitle("Учетная система Олливандера");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель с кнопками
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton btnRestock = new JButton("1. Пополнить склад");
        JButton btnCheckStock = new JButton("2. Проверить остатки склада");
        JButton btnCreateStick = new JButton("3. Создать палочку");
        JButton btnSellStick = new JButton("4. Продать палочку");
        JButton btnViewSticks = new JButton("5. Просмотреть созданные палочки");
        JButton btnClearData = new JButton("6. Обнулить данные");

        // Добавляем кнопки
        panel.add(btnRestock);
        panel.add(btnCheckStock);
        panel.add(btnCreateStick);
        panel.add(btnSellStick);
        panel.add(btnViewSticks);
        panel.add(btnClearData);

        // Обработчики событий
        btnRestock.addActionListener(e -> new RestockGUI(MainFrame.this).setVisible(true));

        btnCheckStock.addActionListener(e -> new StockViewer().setVisible(true));
        btnCreateStick.addActionListener(e -> new CreateStickGUI(MainFrame.this).setVisible(true));
        btnSellStick.addActionListener(e -> new SellStickGUI(MainFrame.this).setVisible(true));
        btnViewSticks.addActionListener(e -> new StickViewer().setVisible(true));
        btnClearData.addActionListener(e -> confirmAndClear());

        add(panel);
        setVisible(true);
    }

    private void confirmAndClear() {
        int option = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите обнулить все данные?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            DatabaseCleaner.clearAllData();
        }
    }

    
}
