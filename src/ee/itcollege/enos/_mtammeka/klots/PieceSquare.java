package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 15.01.2017.
 */
public class PieceSquare implements TetrisSquare {
    private final static byte EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2, FIXED_SQUARE = 3;
    private byte status = EMPTY_SQUARE;
    public void setOccupied() {
        this.status = OCCUPIED_SQUARE;
    }

    public boolean isOccupied() {
        return (status == OCCUPIED_SQUARE);
    }

    public void setFixed() {
        status = FIXED_SQUARE;
    }

    public boolean isFixed() {
        return (status == FIXED_SQUARE);
    }

    public void setEmpty() {
        status = EMPTY_SQUARE;
    }

    public boolean isEmpty() {
        return (status == EMPTY_SQUARE);
    }
}
