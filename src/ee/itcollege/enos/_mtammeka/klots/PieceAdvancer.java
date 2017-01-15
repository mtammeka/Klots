package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 15.01.2017.
 */
public class PieceAdvancer {
    static boolean advance(GameSquare[][] board) {
        int boardHeight = board.length;
        int boardWidth = board[0].length;
        boolean nextSquareDownIsFixed = true;
        
        for (int c = 0; c < boardWidth; c++) {
            for (int r = boardHeight - 1; r >= 0; r--) {
                if (board[r][c].isOccupied() && nextSquareDownIsFixed) {
                    fixAllOccupied(board);
                    return false; // false st fallingPieceExists = false!
                }
                nextSquareDownIsFixed = board[r][c].isFixed();
            }
            nextSquareDownIsFixed = true;
        }

        // siia jõuab vaid siis kui on kindlasti ruumi klotsi edasinihutamiseks
        int occupiedCountOnThisRow = 0;
        for (int c = 0; c < boardWidth; c++) {
            for (int r = boardHeight - 1; r >= 0; r--) {
                if (board[r][c].isOccupied()) {
                    occupiedCountOnThisRow++;
                    // nihutatavate klotside hulk on loetud kui jõuame tühja VÕI fikseeritud ruuduni
                    // (lateraalliikumine võimaldab mängijal pala lükata mõne fikseeritud ruudu alla!)
                    if (r == 0 || (board[r - 1][c].isEmpty() || board[r - 1][c].isFixed())) {
                        board[r][c].setEmpty();
                        board[r + occupiedCountOnThisRow][c].setOccupied();
                        // selles tulbas peaks kõik nihutatud olema mida nihutada vaja
                        break;
                    }
                }
            }
            occupiedCountOnThisRow = 0;
        }
        return true;
    }
    
    private static void fixAllOccupied(GameSquare[][] board) {
        for (int k = 0; k < board.length; k++) {
            for (int l = 0; l < board[k].length; l++) {
                if (board[k][l].isOccupied())
                    board[k][l].setFixed();
            }
        }
    }
}
