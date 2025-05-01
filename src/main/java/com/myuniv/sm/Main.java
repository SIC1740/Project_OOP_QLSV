package com.myuniv.sm;

import com.myuniv.sm.util.AppConfig;
import com.myuniv.sm.view.MainFrame;
import com.myuniv.sm.view.LoginFrame;
import com.myuniv.sm.view.admin.AdminFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true)
        );
    }
}
