package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 15.01.2017.
 */
public interface TetrisSquare {
    void setOccupied();
    boolean isOccupied();

    void setFixed();
    boolean isFixed();

    void setEmpty();
    boolean isEmpty();
}
