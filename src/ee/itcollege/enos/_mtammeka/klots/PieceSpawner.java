package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 15.01.2017.
 */
public class PieceSpawner {
    static boolean spawn(GameSquare[][] board, PieceSquare[][] piece) {
        int pieceWidth = piece[0].length;
        int pieceHeight = piece.length;
        int startRow = 0, startColumn = (board[0].length / 2) - (pieceWidth / 2);

        // see tsükkel on ainult kontrollimiseks
        for (int r = 0; r < pieceHeight; r++) {
            for (int c = 0; c < pieceWidth; c++) {
                if (piece[r][c].isOccupied()) {
                    try {
                        GameSquare square = board[startRow + r][startColumn + c];
                        if (!square.isEmpty()) {
                            return false; // mäng läbi
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return false; // mäng läbi... esimest spawni tehes ei tohiks nagu siia sattuda ka
                    }
                }
            }
        }

        // aga kui siia jõutud siis spawnimiseks ruumi on
        for (int r = 0; r < pieceHeight; r++) {
            for (int c = 0; c < pieceWidth; c++) {
                copyState(piece[r][c], board[startRow + r][startColumn + c]);
            }
        }
        return true;
    }
    public static void copyState(TetrisSquare from, TetrisSquare to) {
        if (from.isFixed()) {
            to.setFixed();
        } else if (from.isOccupied()) {
            to.setOccupied();
        } else {
            to.setEmpty();
        }
    }
}
