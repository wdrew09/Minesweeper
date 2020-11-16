import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends JFrame implements MouseListener {

    private Board gameBoard;
    private BoardPiece boardPiece;

    private JFrame frame;

    private JLabel bombLabel, timerLabel;
    private JPanel boardView, labelView;

    private JMenuBar menuBar;
    private JMenu gameMenu, setupMenuItem, helpMenu;
    private JMenuItem newMenuItem,  exitMenuItem, easyMenuItem, mediumMenuItem, hardMenuItem;

    int timer = 0;

    private int size = 64;
    private int bombCount = 10;

    int bombs = bombCount;

    private boolean[] pieceFlipped = new boolean[144];

    private boolean[] whiteSpacePieces = new boolean[144];

    private Board board;

    private Boolean isFirstClick = true;

    private Boolean didEnd = false;

    long startTime = System.currentTimeMillis();

    public Minesweeper() {
        super("Minesweeper");

        startGame();

        startTime = System.currentTimeMillis();
        long currentTime = (System.currentTimeMillis()-startTime)/1000;
        while (!didEnd) {
            long time = (System.currentTimeMillis()-startTime)/1000;
            if (time != currentTime) {
                timerLabel.setText("Timer: " + time);
            }
        }
    }

    public void startGame() {
        timerLabel = new JLabel("Timer: " + timer);
        bombLabel = new JLabel("Bombs: " + bombs);

        labelView = new JPanel();  // used to hold labels
        boardView = new JPanel();  // used to hold game board

        frame = new JFrame();

        menuBar = new JMenuBar();
        gameMenu = new JMenu("Game");
        helpMenu = new JMenu("Help");
        newMenuItem = new JMenuItem(new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard(size);
            }
        });
        setupMenuItem = new JMenu("Setup");

        easyMenuItem = new JMenuItem(new AbstractAction("Easy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard(16);
            }
        });
        mediumMenuItem = new JMenuItem(new AbstractAction("Medium") {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard(64);
            }
        });
        hardMenuItem = new JMenuItem(new AbstractAction("Hard") {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard(144);
            }
        });
        setupMenuItem.add(easyMenuItem);
        setupMenuItem.add(mediumMenuItem);
        setupMenuItem.add(hardMenuItem);

        exitMenuItem = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }
        });

        gameMenu.add(newMenuItem);
        gameMenu.add(setupMenuItem);
        gameMenu.add(exitMenuItem);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);

        Container c = getContentPane();

        gameBoard = new Board(size,bombCount, size/2, this);
        board = gameBoard;

        boardView.setLayout(new GridLayout((int) Math.sqrt(size), (int) Math.sqrt(size), 0, 0));
        gameBoard.fillBoardView(boardView);

        labelView.setLayout(new GridLayout(1, 4, 0, 0));
        labelView.add(timerLabel);
        labelView.add(bombLabel);

        c.add(labelView, BorderLayout.NORTH);
        c.add(boardView, BorderLayout.SOUTH);

        frame.add(c);

        frame.setJMenuBar(menuBar);;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Minesweeper");
        frame.pack();
        frame.setSize((int) Math.sqrt(size)*50+20, (int) Math.sqrt(size)*50 + 200);
        frame.setVisible(true);

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        boolean wasFirstClick = false;

        BoardPiece currPiece = (BoardPiece) e.getSource();

        String currPieceIndex = currPiece.getIcon().toString();
        currPieceIndex = currPieceIndex.substring((currPieceIndex.lastIndexOf("/")));

        if (isFirstClick) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (!currPieceIndex.contains("minesweeper_flagged.png")) {
                    currPiece.showFlagged();
                    bombs -= 1;
                    bombLabel.setText("Bombs: " + bombs);
                }
            } else {
                if (currPieceIndex.contains("minesweeper_flagged.png")) {
                    currPiece.hideFront();
                    bombs += 1;
                    bombLabel.setText("Bombs: " + bombs);
                } else {
                    if (currPiece.getIcon().toString().contains("/minesweeper_0.png")) {
                        whiteSpacePieces[currPiece.id()] = true;
                        currPiece.showFront();
                        findWhiteSpace();
                    }

                    wasFirstClick = true;
                    boardView.removeAll();
                    gameBoard = new Board(size, bombCount, currPiece.id(), this);
                    board = gameBoard;

                    boardView.setLayout(new GridLayout((int) Math.sqrt(size), (int) Math.sqrt(size), 0, 0));
                    gameBoard.fillBoardView(boardView);

                    isFirstClick = false;
                    revalidate();
                }
            }
        } else {


            if (!pieceFlipped[currPiece.id()]) {

                if (SwingUtilities.isRightMouseButton(e)) {
                    if (!currPieceIndex.contains("minesweeper_flagged.png")) {
                        currPiece.showFlagged();
                        bombs -= 1;
                        bombLabel.setText("Bombs: " + bombs);
                    }
                } else {
                    if (currPieceIndex.contains("minesweeper_flagged.png")) {
                        currPiece.hideFront();
                        bombs += 1;
                        bombLabel.setText("Bombs: " + bombs);
                    } else {
                        pieceFlipped[currPiece.id()] = true;

                        if (currPieceIndex.contains("/minesweeper_facingDown.png")) {
                            currPiece.showFront();
                        }

                        if (currPiece.getIcon().toString().contains("/minesweeper_bomb.png")) {
                            showAll();
                        }
                        if (currPiece.getIcon().toString().contains("/minesweeper_0.png")) {
                            whiteSpacePieces[currPiece.id()] = true;
                            currPiece.showFront();
                            findWhiteSpace();
                        }
                    }
                }
            }
        }

        if (wasFirstClick) {
            board.getPieces()[currPiece.id()].showFront();
        }
        frame.setVisible(true);

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void findWhiteSpace() {
        boolean whiteSpaceFound = false;

        for (int i = 0; i < size; i++) {

            if (whiteSpacePieces[i]) {

                int row = i / ((int) (Math.sqrt(size)));
                int column = i % ((int) (Math.sqrt(size)));

                String currPieceIndex = board.getPieces()[i].getIcon().toString();
                currPieceIndex = currPieceIndex.substring((currPieceIndex.lastIndexOf("/")));

                if (row - 1 >= 0) {
                    currPieceIndex = board.getPieces()[i-(int) (Math.sqrt(size))].getIcon().toString();
                    currPieceIndex = currPieceIndex.substring((currPieceIndex.lastIndexOf("/")));
                    if (board.getFront(i - (int) (Math.sqrt(size))) && !currPieceIndex.contains("/minesweeper_0.png")) {
                        board.getPieces()[i-(int) (Math.sqrt(size))].showFront();
                        pieceFlipped[i-(int) (Math.sqrt(size))] = true;
                        whiteSpacePieces[i-(int) (Math.sqrt(size))] = true;
                        whiteSpaceFound = true;
                    }
                }

                if (column - 1 >= 0) {
                    currPieceIndex = board.getPieces()[i-1].getIcon().toString();
                    currPieceIndex = currPieceIndex.substring((currPieceIndex.lastIndexOf("/")));
                    if (board.getFront(i-1) && !currPieceIndex.contains("/minesweeper_0.png")) {
                        board.getPieces()[i-1].showFront();
                        pieceFlipped[i-1] = true;
                        whiteSpacePieces[i-1] = true;
                        whiteSpaceFound = true;
                    }
                }

                if (column + 1 <= Math.sqrt(size)-1) {
                    currPieceIndex = board.getPieces()[i+1].getIcon().toString();
                    currPieceIndex = currPieceIndex.substring((currPieceIndex.lastIndexOf("/")));
                    if (board.getFront(i+1) && !currPieceIndex.contains("/minesweeper_0.png")) {
                        board.getPieces()[i+1].showFront();
                        pieceFlipped[i+1] = true;
                        whiteSpacePieces[i+1] = true;
                        whiteSpaceFound = true;
                    }
                }

                if (row + 1 <= Math.sqrt(size)-1) {
                    currPieceIndex = board.getPieces()[i+(int) (Math.sqrt(size))].getIcon().toString();
                    currPieceIndex = currPieceIndex.substring((currPieceIndex.lastIndexOf("/")));
                    if (board.getFront(i+(int) (Math.sqrt(size))) && !currPieceIndex.contains("/minesweeper_0.png")) {
                        board.getPieces()[i+(int) (Math.sqrt(size))].showFront();
                        pieceFlipped[i+(int) (Math.sqrt(size))] = true;
                        whiteSpacePieces[i+(int) (Math.sqrt(size))] = true;
                        whiteSpaceFound = true;
                    }
                }
            }
        }

        if (whiteSpaceFound) {
            findWhiteSpace();
        } else {
            for (int i = 0; i <= size - 1; i++) {
                int row = i / ((int) (Math.sqrt(size)));
                int column = i % ((int) (Math.sqrt(size)));
                if (whiteSpacePieces[i]) {
                    if (row + 1 <= Math.sqrt(size)-1) {
                        board.getPieces()[i+(int) (Math.sqrt(size))].showFront();
                        pieceFlipped[i+(int) (Math.sqrt(size))] = true;
                    }
                    if (row - 1 >= 0) {
                        board.getPieces()[i-(int) (Math.sqrt(size))].showFront();
                        pieceFlipped[i-(int) (Math.sqrt(size))] = true;
                    }
                    if (column + 1 <= Math.sqrt(size)-1) {
                        board.getPieces()[i+1].showFront();
                        pieceFlipped[i+1] = true;
                    }
                    if (column - 1 >= 0) {
                        board.getPieces()[i-1].showFront();
                        pieceFlipped[i-1] = true;
                    }
                }
            }
        }
    }

    public void showAll() {
        didEnd = true;
        for (int i = 0; i <= size - 1; i++) {
            board.getPieces()[i].showFront();
            pieceFlipped[i] = true;
        }
        for (int i = 0; i <= size - 1; i++) {
            board.getPieces()[i].showFront();
            pieceFlipped[i] = true;
        }

        revalidate();
        repaint();
        setVisible(true);
        frame.setVisible(true);
    }

    public void resetBoard(int sizeVal) {
        size = sizeVal;
        if (sizeVal == 16) {
            bombCount = 4;
        } else if (sizeVal == 64) {
            bombCount = 15;
        } else if (sizeVal == 144) {
            bombCount = 40;
        }
        didEnd = false;
        boardView.removeAll();
        gameBoard = new Board(size, bombCount, size / 2, this);
        board = gameBoard;

        boardView.setLayout(new GridLayout((int) Math.sqrt(sizeVal), (int) Math.sqrt(sizeVal), 0, 0));
        gameBoard.fillBoardView(boardView);

        frame.setSize((int) Math.sqrt(sizeVal)*50+20, (int) Math.sqrt(sizeVal)*50 + 200);

        startTime = System.currentTimeMillis();
        bombs = bombCount;
        bombLabel.setText("Bombs: " + bombCount);

        isFirstClick = true;
        revalidate();
        repaint();
        setVisible(true);
        for (int i = 0; i <= pieceFlipped.length - 1; i++) {
            pieceFlipped[i] = false;
        }
        frame.setVisible(true);
    }


    public static void main(String args[])
    {
        Minesweeper M = new Minesweeper();
        M.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}
