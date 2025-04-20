package com.myuniv.sm.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(String title) {
        super(title);
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Exit"))
                .addActionListener(e -> dispose());
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.add(new JLabel("Welcome to Student Manager", SwingConstants.CENTER), "home");
        add(mainPanel, BorderLayout.CENTER);
    }
}
