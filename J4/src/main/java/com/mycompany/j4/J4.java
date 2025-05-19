/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.j4;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import javax.swing.SwingUtilities;

/**
 *
 * @author Владислав
 */
public class J4 {

    public static void main(String[] args) {
        
        try {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));
        SwingUtilities.invokeLater(() -> new MainFrame());
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    }
    }


