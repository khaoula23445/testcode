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

        for (int i = 0; i < NUM_IMAGES; i++) {
            img[i] = new ImageIcon(basePath + i + ".gif").getImage();
        }

        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    public void newGame() {
        Random random = new Random();

        inGame = true;
        minesLeft = mines;
        allCells = rows * cols;
        field = new int[allCells];

        for (int i = 0; i < allCells; i++) {
            field[i] = COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(minesLeft));

        for (int i = 0; i < mines; i++) {
            int position = random.nextInt(allCells);

            if (field[position] != COVERED_MINE_CELL) {
                field[position] = COVERED_MINE_CELL;
                incrementNeighborCells(position);
            } else {
                i--; // Retry if the position already has a mine
            }
        }
    }

    private void incrementNeighborCells(int position) {
        int[] directions = {-1, 0, 1};

        for (int dRow : directions) {
            for (int dCol : directions) {
                if (dRow == 0 && dCol == 0) continue; // Skip the mine itself

                int neighbor = position + dRow * cols + dCol;
                if (neighbor >= 0 && neighbor < allCells && field[neighbor] != COVERED_MINE_CELL) {
                    field[neighbor]++;
                }
            }
        }
    }

    public void findEmptyCells(int index) {
       

        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;

                int neighbor = index + dRow * cols + dCol;
                if (neighbor >= 0 && neighbor < allCells && field[neighbor] > MINE_CELL) {
                    field[neighbor] -= COVER_FOR_CELL;
                    if (field[neighbor] == EMPTY_CELL) {
                        findEmptyCells(neighbor);
                    }
                }
            }
        }
    }

    public void paint(Graphics g) {
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cell = field[(i * cols) + j];

                if (inGame && cell == MINE_CELL) {
                    inGame = false;
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

        if (uncover == 0 && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame) {
            statusbar.setText("Game lost");
        }
    }

    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int col = x / CELL_SIZE;
            int row = y / CELL_SIZE;

            if (!inGame) {
                newGame();
                repaint();
            }

            if (x < cols * CELL_SIZE && y < rows * CELL_SIZE) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    handleRightClick(row, col);
                } else {
                    handleLeftClick(row, col);
                }
            }
        }

        private void handleRightClick(int row, int col) {
            int index = row * cols + col;

            if (field[index] > MINE_CELL) {
                if (field[index] <= COVERED_MINE_CELL) {
                    if (minesLeft > 0) {
                        field[index] += MARK_FOR_CELL;
                        minesLeft--;
                        statusbar.setText(Integer.toString(minesLeft));
                    } else {
                        statusbar.setText("No marks left");
                    }
                } else {
                    field[index] -= MARK_FOR_CELL;
                    minesLeft++;
                    statusbar.setText(Integer.toString(minesLeft));
                }
            }
        }

        private void handleLeftClick(int row, int col) {
            int index = row * cols + col;

            if (field[index] > COVERED_MINE_CELL) return;

            if (field[index] > MINE_CELL && field[index] < MARKED_MINE_CELL) {
                field[index] -= COVER_FOR_CELL;

                if (field[index] == MINE_CELL) {
                    inGame = false;
                }

                if (field[index] == EMPTY_CELL) {
                    findEmptyCells(index);
                }
            }

            repaint();
        }
    }
}
