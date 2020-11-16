import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Board {

    //Array that will have all the pieces, row and column
    private BoardPiece[] pieces;

    private Boolean[] whitePieces;

    // Resource loader
    private ClassLoader loader = getClass().getClassLoader();
    private Object BoardPiece;

    public Board(int size, int mineCount, int firstClicked, MouseListener AL) {
        boolean[][] bombs = new boolean[(int)Math.sqrt(size)][(int)Math.sqrt(size)];
        pieces = new BoardPiece[size];
        whitePieces = new Boolean[size];

        //Placing all the bombs
        for (int i = 0; i < mineCount; i++) {
            Random r = new Random();
            int rand = r.nextInt(size);

            if (rand == firstClicked) {
            }

            while (pieces[rand] != null || rand == firstClicked) {
                rand = r.nextInt(size);
            }

            String imagePath = "res/minesweeper_bomb.png";
            ImageIcon img = new ImageIcon(loader.getResource(imagePath));

            BoardPiece p = new BoardPiece(img);

            p.addMouseListener(AL);
            p.setID(rand);
            pieces[rand] = p;

            int randRow = rand / ((int) (Math.sqrt(size)));
            int randColumn = rand % ((int) (Math.sqrt(size)));


            bombs[randRow][randColumn] = true;

        }

        //Placing all the other pieces
        for (int i = 0; i < size - mineCount; i++) {
            Random r = new Random();
            int rand = r.nextInt(size);

            int bombsTouchingCount = 0;

            while (pieces[rand] != null) {
                rand = r.nextInt(size);
            }

            int randRow = rand / ((int) (Math.sqrt(size)));
            int randColumn = rand % ((int) (Math.sqrt(size)));

            bombs[randRow][randColumn] = false;



            if (randRow - 1 >= 0) {
                if (randColumn - 1 >= 0) {
                    if (bombs[randRow - 1][randColumn - 1] == true) {
                        bombsTouchingCount += 1;
                    }
                }
                if (bombs[randRow - 1][randColumn] == true) {
                    bombsTouchingCount += 1;
                }
                if (randColumn + 1 <= Math.sqrt(size) - 1) {
                    if (bombs[randRow - 1][randColumn + 1] == true) {
                        bombsTouchingCount += 1;
                    }
                }
            }
            if (randColumn - 1 >= 0) {
                if (bombs[randRow][randColumn - 1] == true) {
                    bombsTouchingCount += 1;
                }
            }
            if (randColumn + 1 <= Math.sqrt(size) - 1) {
                if (bombs[randRow][randColumn + 1] == true) {
                    bombsTouchingCount += 1;
                }
            }
            if (randRow + 1 <= Math.sqrt(size) - 1) {
                if (randColumn - 1 >= 0) {
                    if (bombs[randRow + 1][randColumn - 1] == true) {
                        bombsTouchingCount += 1;
                    }
                }
                if (bombs[randRow + 1][randColumn] == true) {
                    bombsTouchingCount += 1;
                }
                if (randColumn + 1 <= Math.sqrt(size) - 1) {
                    if (bombs[randRow + 1][randColumn + 1] == true) {
                        bombsTouchingCount += 1;
                    }
                }
            }

            if (bombsTouchingCount == 0) {
                whitePieces[rand] = true;
            }

            String imagePath = "res/minesweeper_"+ bombsTouchingCount + ".png";
            ImageIcon img = new ImageIcon(loader.getResource(imagePath));

            BoardPiece p = new BoardPiece(img);

            p.addMouseListener(AL);
            p.setID(rand);
            pieces[rand] = p;
        }

    }

    public BoardPiece[] getPieces() { return pieces; }

    public boolean getFront(int id) {
        if (whitePieces[id] != null) {
            if (whitePieces[id]) {
                return true;
            }
        }
        return false;
    }

    public void fillBoardView(JPanel view)
    {
        for (BoardPiece c : pieces) {
            view.add(c);
        }
    }
}
