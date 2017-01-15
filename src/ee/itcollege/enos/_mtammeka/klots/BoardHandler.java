package ee.itcollege.enos._mtammeka.klots;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Madis on 14.01.2017.
 */
public class BoardHandler {

    /* BoardHandler võiks kutsuda PieceImporteri ja saada sealt array Pice objekte */
    private int rows, columns;
    private GameSquare[][] board;
    private boolean fallingPieceExists = false, isGameOver = false;
    private PieceSquare[][] currentPiece;

    private ArrayList<Piece> pieces;

    BoardHandler(GameSquare[][] board) {
        this.board = board;
        rows = board.length;
        columns = board[0].length; // eeldame ühepikkuseid ridu!!

        try {
            pieces = new PieceImporter().getPieces();
        } catch (IOException e) {
            System.out.println("pieces.txt failis ilmselt midagi valesti");
            System.out.println(e);
        }
    }

    public void doSomething() {
        board[(int) (Math.random() * rows)][(int) (Math.random() * columns)].setOccupied();
    }

    public void advanceTheGame() {
        /* Kui fallingPieceExists = false, spawnime _pieces_ seest ühe sellise.
         * st määrame suvalise piece currentPiece-ks;
          *
          * ja proovime spawnida kui ruumi on, kui pole ruumi on gameover
          *
          * kui on piece olemas;
          * vaatame kas on ruumi alla liigutada ja liigutame siis*/
        if (!fallingPieceExists) {
            currentPiece = pieces.get((int)(pieces.size() * Math.random())).getPiece();

            isGameOver = !PieceSpawner.spawn(board, currentPiece);
            fallingPieceExists = true;
        } else {
            // üritame edasi liigutada
            fallingPieceExists = PieceAdvancer.advance(board); // true kui ei fikseerinud klotsi ära
        }

    }
}
