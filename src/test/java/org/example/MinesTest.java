package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class MinesTest {

    private Mines mines;

    @BeforeEach
    public void setUp() {
        // Create a new instance of the Mines class before each test
        mines = new Mines();
    }

    @Test
    public void testWindowSize() {
        // Verify that the window size is correct
        assertEquals(250, mines.getWidth(), "Window width should be 250px.");
        assertEquals(290, mines.getHeight(), "Window height should be 290px.");
    }

    @Test
    public void testStatusBarInitialText() {
        // Get the status bar from the content pane
        JLabel statusLabel = (JLabel) mines.getContentPane().getComponent(1);
        assertNotNull(statusLabel, "Status bar should be present.");
        assertEquals("Mines left: 40", statusLabel.getText(), "Initial status text should be 'Mines left: 40'.");
    }

    @Test
    public void testStatusBarUpdate() {
        // Get the status bar from the content pane
        JLabel statusLabel = (JLabel) mines.getContentPane().getComponent(1);
        
        // Simulate changing the mines left text
        statusLabel.setText("Mines left: 39");
        
        // Verify the updated text
        assertEquals("Mines left: 39", statusLabel.getText(), "Status bar should update to reflect remaining mines.");
    }

    @Test
    public void testBoardIsAddedToFrame() {
        // Retrieve the Board component (which is the first component in the content pane)
        Board board = (Board) mines.getContentPane().getComponent(0);
        assertNotNull(board, "Game board should be present in the JFrame.");
    }

    @Test
    public void testWindowCloseOperation() {
        // Simulate closing the window
        mines.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        assertEquals(JFrame.EXIT_ON_CLOSE, mines.getDefaultCloseOperation(), "Window close operation should be EXIT_ON_CLOSE.");
    }
}
