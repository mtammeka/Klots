package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 14.01.2017.
 */
public class BoardHandler {

    /* BoardHandler võiks kutsuda PieceImporteri ja saada sealt array Pice objekte */
    private int ROWS, COLUMNS;
    GameSquare[][] board;
    BoardHandler(GameSquare[][] board) {
        this.board = board;
        ROWS = board.length;
        COLUMNS = board[0].length; // eeldame ühepikkuseid ridu!!
    }

    public void doSomething() {
        board[(int) (Math.random() * ROWS)][(int) (Math.random() * COLUMNS)].setOccupied();
    }
}
