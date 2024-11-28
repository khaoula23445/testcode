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

    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int mines_left;
    private Image[] img;
    private int mines = 40;
    private int rows = 16;
    private int cols = 16;
    private int all_cells;
    private JLabel statusbar;

    public Board(JLabel statusbar) {
        this.statusbar = statusbar;

        img = new Image[NUM_IMAGES];
        loadImages();

        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    private void loadImages() {
        for (int i = 0; i < NUM_IMAGES; i++) {
            img[i] = new ImageIcon(getClass().getResource("/images/" + i + ".gif")).getImage();
        }
    }

    public void newGame() {
        Random random = new Random();
        int i = 0;
        int position;

        inGame = true;
        mines_left = mines;
        all_cells = rows * cols;
        field = new int[all_cells];

        for (int j = 0; j < all_cells; j++) {
            field[j] = COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(mines_left));

        while (i < mines) {
            position = random.nextInt(all_cells);

            if (field[position] != COVERED_MINE_CELL) {
                field[position] = COVERED_MINE_CELL;
                incrementAdjacentCells(position);
                i++;
            }
        }
    }

    private void incrementAdjacentCells(int position) {
        int[] directions = {-1, 1, -cols, cols, -cols - 1, -cols + 1, cols - 1, cols + 1};
        int current_col = position % cols;

        for (int dir : directions) {
            int neighbor = position + dir;

            if (isValidNeighbor(position, neighbor, dir, current_col)) {
                if (field[neighbor] != COVERED_MINE_CELL) {
                    field[neighbor]++;
                }
            }
        }
    }

    private boolean isValidNeighbor(int position, int neighbor, int dir, int current_col) {
        boolean withinBounds = neighbor >= 0 && neighbor < all_cells;
        boolean notOutOfRow = (dir == -1 && current_col > 0) || (dir == 1 && current_col < cols - 1) || Math.abs(dir) > 1;
        return withinBounds && notOutOfRow;
    }

    public void findEmptyCells(int j) {
        int[] directions = {-1, 1, -cols, cols, -cols - 1, -cols + 1, cols - 1, cols + 1};
        int current_col = j % cols;

        for (int dir : directions) {
            int neighbor = j + dir;

            if (isValidNeighbor(j, neighbor, dir, current_col) && field[neighbor] > MINE_CELL) {
                field[neighbor] -= COVER_FOR_CELL;
                if (field[neighbor] == EMPTY_CELL) {
                    findEmptyCells(neighbor);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        int cell;
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cell = field[(i * cols) + j];

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
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            if (!inGame) {
                newGame();
                repaint();
                return;
            }

            if (x < cols * CELL_SIZE && y < rows * CELL_SIZE) {
                int index = cRow * cols + cCol;

                if (e.getButton() == MouseEvent.BUTTON3) {
                    toggleMark(index);
                } else {
                    uncoverCell(index);
                }

                repaint();
            }
        }

        private void toggleMark(int index) {
            if (field[index] > MINE_CELL) {
                if (field[index] <= COVERED_MINE_CELL && mines_left > 0) {
                    field[index] += MARK_FOR_CELL;
                    mines_left--;
                } else if (field[index] > COVERED_MINE_CELL) {
                    field[index] -= MARK_FOR_CELL;
                    mines_left++;
                }
                statusbar.setText(Integer.toString(mines_left));
            }
        }

        private void uncoverCell(int index) {
            if (field[index] > COVERED_MINE_CELL) {
                return;
            }

            if (field[index] > MINE_CELL && field[index] < MARKED_MINE_CELL) {
                field[index] -= COVER_FOR_CELL;

                if (field[index] == MINE_CELL) {
                    inGame = false;
                } else if (field[index] == EMPTY_CELL) {
                    findEmptyCells(index);
                }
            }
        }
    }
}
