import javax.swing.*;

public class BoardPiece extends JButton {

    //Loads resources
    private ClassLoader loader = getClass().getClassLoader();

    //Initializing the different ways the pieces can be shown
    private Icon front;
    private Icon back = new ImageIcon(loader.getResource("res/minesweeper_facingDown.png"));
    private Icon flagged = new ImageIcon(loader.getResource("res/minesweeper_flagged.png"));

    private int id;

    public BoardPiece() {super();}

    public BoardPiece(ImageIcon frontImage) {
        super();
        front = frontImage;
        super.setIcon(back);
    }

    // Set the image used as the front of the card
    public void setFrontImage(ImageIcon frontImage) {
        setIcon(frontImage);
    }

    // Board piece flipping functions
    public void showFront() {
        setFrontImage((ImageIcon) front);
    }

    public void hideFront() {
        setFrontImage((ImageIcon) back);
    }

    public void showFlagged() {
        setFrontImage((ImageIcon) flagged);
    }

    public int id() { return id; }
    public void setID(int location) {
        id = location;
    }
}
