package org.example;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class Mines extends JFrame {
    private static final long serialVersionUID = 4772165125287256837L;

    // Constants for window dimensions
    private static final int WIDTH = 250;
    private static final int HEIGHT = 290;

    private JLabel statusbar;

    public Mines() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Minesweeper");

        statusbar = new JLabel("Mines left: 40");
        add(statusbar, BorderLayout.SOUTH);

        Board board = new Board(statusbar);
        add(board, BorderLayout.CENTER);

        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Mines();
    }
}
