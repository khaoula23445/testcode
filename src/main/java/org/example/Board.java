package org.example;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.SecureRandom;  // Import SecureRandom
import java.util.Random;

public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;

    // Constants for the game setup
    private static final int NUM_IMAGES = 13;
    private static final int CELL_SIZE = 15;
    private static final int COVER_FOR_CELL = 10;
    private static final int MARK_FOR_CELL = 10;
    private static final int EMPTY_CELL = 0;
    static final int MINE_CELL = 9;
    static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
    private static final int DRAW_MINE = 9;
    private static final int DRAW_COVER = 10;
    private static final int DRAW_MARK = 11;
    private static final int DRAW_WRONG_MARK = 12;

    // Static final game state variables
    private static final int MINES = 40;
    private static final int ROWS = 16;
    private static final int COLS = 16;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private transient Image[] img; // transient to avoid serialization
    private int allCells;
    private JLabel statusbar;

    // Use SecureRandom for secure random number generation
    private SecureRandom random = new SecureRandom();  // Updated to SecureRandom

    public Board(JLabel statusbar) {
        this.statusbar = statusbar;
        img = new Image[NUM_IMAGES];
        String basePath = "C:\\Users\\NeonTech.DZ\\Downloads\\mineGame\\mineGame\\images\\";

        // Load images
        for (int i = 0; i < NUM_IMAGES; i++) {
            img[i] = new ImageIcon(basePath + i + ".gif").getImage();
        }

        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    // Start a new game
    public void newGame() {
        int position;

        inGame = true;
        minesLeft = MINES;
        allCells = ROWS * COLS;
        field = new int[allCells];

        // Initialize the game field
        for (int i = 0; i < allCells; i++) {
            field[i] = COVER_FOR_CELL;
        }
        statusbar.setText(Integer.toString(minesLeft));

        // Place mines randomly
        int i = 0;
        while (i < MINES) {
            position = random.nextInt(allCells);  // Use SecureRandom for randomness
            if (field[position] != COVERED_MINE_CELL) {
                field[position] = COVERED_MINE_CELL;
                i++;
                // Increment surrounding cells
                incrementSurroundingCells(position);
            }
        }
    }

    // Helper method to increment surrounding cells
    void incrementSurroundingCells(int position) {
        int cell;
        int[] surroundingOffsets = {-1, 0, 1};

        for (int rowOffset : surroundingOffsets) {
            for (int colOffset : surroundingOffsets) {
                if (rowOffset == 0 && colOffset == 0) continue;
                cell = position + rowOffset * COLS + colOffset;
                if (isValidCell(cell)) {
                    field[cell] = (field[cell] != COVERED_MINE_CELL) ? field[cell] + 1 : field[cell];
                }
            }
        }
    }

    // Helper method to check if a cell is valid
    private boolean isValidCell(int cell) {
        return cell >= 0 && cell < allCells;
    }

    // Find and uncover empty cells (recursive flood fill)
    private void findEmptyCells(int j) {
        int cell;

        int[] surroundingOffsets = {-1, 0, 1};
        for (int rowOffset : surroundingOffsets) {
            for (int colOffset : surroundingOffsets) {
                if (rowOffset == 0 && colOffset == 0) continue;
                cell = j + rowOffset * COLS + colOffset;
                if (isValidCell(cell) && field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        findEmptyCells(cell);
                    }
                }
            }
        }
    }

    // Paint the game field on the panel
    @Override
    public void paint(Graphics g) {
        int cell;
        int uncover = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cell = field[(i * COLS) + j];

                // Check for game end condition
                if (inGame && cell == MINE_CELL) {
                    inGame = false;
                }

                if (!inGame) {
                    // Draw game over (loss)
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }
                } else {
                    // Draw covered cells and uncovered cells
                    if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }
                g.drawImage(img[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);
            }
        }

        // Game won condition
        if (uncover == 0 && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame) {
            statusbar.setText("Game lost");
        }
    }

    // Mouse event handling for game actions (left-click and right-click)
    class MinesAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;

            if (!inGame) {
                newGame();
                repaint();
            }

            if ((x < COLS * CELL_SIZE) && (y < ROWS * CELL_SIZE)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // Right-click to mark or unmark a mine
                    if (field[(cRow * COLS) + cCol] > MINE_CELL) {
                        rep = true;
                        handleRightClick(cRow, cCol);
                    }
                } else {
                    // Left-click to uncover a cell
                    if (field[(cRow * COLS) + cCol] > COVERED_MINE_CELL) {
                        return;
                    }
                    if (field[(cRow * COLS) + cCol] > MINE_CELL && field[(cRow * COLS) + cCol] < MARKED_MINE_CELL) {
                        field[(cRow * COLS) + cCol] -= COVER_FOR_CELL;
                        rep = true;
                        if (field[(cRow * COLS) + cCol] == MINE_CELL) {
                            inGame = false;
                        }
                        if (field[(cRow * COLS) + cCol] == EMPTY_CELL) {
                            findEmptyCells((cRow * COLS) + cCol);
                        }
                    }
                }

                if (rep) {
                    repaint();
                }
            }
        }

        private void handleRightClick(int cRow, int cCol) {
            if (field[(cRow * COLS) + cCol] <= COVERED_MINE_CELL) {
                if (minesLeft > 0) {
                    field[(cRow * COLS) + cCol] += MARK_FOR_CELL;
                    minesLeft--;
                    statusbar.setText(Integer.toString(minesLeft));
                } else {
                    statusbar.setText("No marks left");
                }
            } else {
                field[(cRow * COLS) + cCol] -= MARK_FOR_CELL;
                minesLeft++;
                statusbar.setText(Integer.toString(minesLeft));
            }
        }
    }
}
