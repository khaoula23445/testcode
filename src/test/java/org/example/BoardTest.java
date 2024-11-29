package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;
    private JLabel statusbar;

    @BeforeEach
    void setUp() {
        statusbar = new JLabel();
        board = new Board(statusbar);
    }

    @Test
    void testNewGameInitializesFieldCorrectly() {
        board.newGame();
        int[] field = getField(); // Access field via reflection
        assertNotNull(field);
        assertEquals(256, field.length); // ROWS * COLS = 16 * 16
    }

    @Test
    void testMinesArePlacedCorrectly() {
        board.newGame();
        int[] field = getField();
        long mineCount = java.util.Arrays.stream(field)
            .filter(cell -> cell == 19) // Assuming 19 represents mines
            .count();
        assertEquals(40, mineCount); // Ensure 40 mines are placed
    }

    @Test
    void testFindEmptyCells() {
        board.newGame();
        invokePrivateMethod("findEmptyCells", 0); // Test flood-fill behavior
        int[] field = getField();
        assertTrue(field[0] < 10); // Ensure the first cell is uncovered
    }

    @Test
    void testIncrementSurroundingCells() {
        board.newGame();
        int[] field = getField();
        int initialValue = field[1];
        invokePrivateMethod("incrementSurroundingCells", 0); // Increment neighbors of the first cell
        int updatedValue = field[1];
        assertTrue(updatedValue > initialValue); // Neighbor values should increase
    }

    @Test
    void testGameWonCondition() {
        board.newGame();
        int[] field = getField();
        for (int i = 0; i < field.length; i++) {
            if (field[i] == 19) continue; // Skip mines
            field[i] = 0; // Simulate uncovering all safe cells
        }
        board.repaint();
        assertEquals("Game won", statusbar.getText());
    }

    @Test
    void testGameLostCondition() {
        board.newGame();
        int[] field = getField();
        for (int i = 0; i < field.length; i++) {
            if (field[i] == 19) { // Simulate uncovering a mine
                field[i] = 9; // Assuming 9 represents a mine value
                break;
            }
        }
        board.repaint();
        assertEquals("Game lost", statusbar.getText());
    }

    // Utility method to access the private field via reflection
    private int[] getField() {
        try {
            java.lang.reflect.Field field = Board.class.getDeclaredField("field"); // Use explicit type instead of var
            field.setAccessible(true);
            return (int[]) field.get(board);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field", e);
        }
    }

    // Utility method to invoke private methods via reflection
    private void invokePrivateMethod(String methodName, int arg) {
        try {
            java.lang.reflect.Method method = Board.class.getDeclaredMethod(methodName, int.class); // Use explicit type
            method.setAccessible(true); // Make the method accessible
            method.invoke(board, arg); // Invoke the method on the board instance
        } catch (Exception e) {
            e.printStackTrace(); // Print any reflection-related errors
        }
    }
}
