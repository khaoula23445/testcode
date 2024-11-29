package org.example;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;

    private static final int NUM_IMAGES = 13;
    private static final int CELL_SIZE = 15;
    private static final int COVER_FOR_CELL = 10;
    private static final int MARK_FOR_CELL = 10;
    private static final int EMPTY_CELL = 0;
    private static final int MINE_CELL = 9;
    private static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
    private static final int DRAW_MINE = 9;
    private static final int DRAW_COVER = 10;
    private static final int DRAW_MARK = 11;
    private static final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;
    private int mines = 40;
    private int rows = 16;
    private int cols = 16;
    private int allCells;
    private JLabel statusbar;

    public Board(JLabel statusbar) {
        this.statusbar = statusbar;
        this.img = new Image[NUM_IMAGES];
        String basePath = "C:\\Users\\NeonTech.DZ\\Downloads\\mineGame\\mineGame\\images\\";

        // Load images for game tiles
        for (int i = 0; i < NUM_IMAGES; i++) {
            img[i] = new ImageIcon(basePath + i + ".gif").getImage();
        }

        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    // Initializes a new game
    public void newGame() {
        Random random = new Random();
        int position, cell, currentCol;

        inGame = true;
        minesLeft = mines;
        allCells = rows * cols;
        field = new int[allCells];

        // Set all cells as covered
        for (int i = 0; i < allCells; i++) {
            field[i] = COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(minesLeft));

        // Randomly place mines on the board
        for (int i = 0; i < mines; i++) {
            position = random.nextInt(allCells);

            if (field[position] != COVERED_MINE_CELL) {
                currentCol = position % cols;
                field[position] = COVERED_MINE_CELL;

                // Increment the surrounding cells' values to indicate proximity to mines
                incrementNeighborCells(position, currentCol);
            } else {
                i--; // Retry if mine placement fails
            }
        }
    }

    // Helper method to increment the neighboring cells around a mine
    private void incrementNeighborCells(int position, int currentCol) {
        int cell;
        int[] directions = {-1, 0, 1}; // Directions to check the neighbors

        for (int dRow : directions) {
            for (int dCol : directions) {
                if (dRow == 0 && dCol == 0) continue; // Skip the center cell (it is the mine itself)
                
                cell = position + dRow * cols + dCol;
                if (cell >= 0 && cell < allCells && field[cell] != COVERED_MINE_CELL) {
                    field[cell]++;
                }
            }
        }
    }

    // Recursive method to uncover cells that are empty
    public void findEmptyCells(int j) {
        int currentCol = j % cols;
        int cell;

        // Uncover all adjacent cells if the current cell is empty
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;

                cell = j + dRow * cols + dCol;
                if (cell >= 0 && cell < allCells && field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        findEmptyCells(cell); // Recursive call for empty cells
                    }
                }
            }
        }
    }

    // Paint the game board (called automatically when the board needs to be redrawn)
    public void paint(Graphics g) {
        int uncover = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cell = field[(i * cols) + j];

                if (inGame && cell == MINE_CELL) {
                    inGame = false; // Game over when mine is uncovered
                }

                if (!inGame) {
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

    // MouseAdapter to handle user interactions (clicks and right-clicks)
    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            if (!inGame) {
                newGame();
                repaint();
            }

            if (x < cols * CELL_SIZE && y < rows * CELL_SIZE) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    handleRightClick(cRow, cCol);
                } else {
                    handleLeftClick(cRow, cCol);
                }
            }
        }

        private void handleRightClick(int cRow, int cCol) {
            if (field[(cRow * cols) + cCol] > MINE_CELL) {
                if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                    if (minesLeft > 0) {
                        field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                        minesLeft--;
                        statusbar.setText(Integer.toString(minesLeft));
                    } else {
                        statusbar.setText("No marks left");
                    }
                } else {
                    field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                    minesLeft++;
                    statusbar.setText(Integer.toString(minesLeft));
                }
            }
        }

        private void handleLeftClick(int cRow, int cCol) {
            if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) return;

            if (field[(cRow * cols) + cCol] > MINE_CELL && field[(cRow * cols) + cCol] < MARKED_MINE_CELL) {
                field[(cRow * cols) + cCol] -= COVER_FOR_CELL;

                if (field[(cRow * cols) + cCol] == MINE_CELL) {
                    inGame = false;
                }

                if (field[(cRow * cols) + cCol] == EMPTY_CELL) {
                    findEmptyCells((cRow * cols) + cCol);
                }
            }

            repaint();
        }
    }
}
