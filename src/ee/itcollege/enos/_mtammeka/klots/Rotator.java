package ee.itcollege.enos._mtammeka.klots;

import java.util.ArrayList;

/**
 * Created by Madis on 15.01.2017.
 */
public class Rotator {
    static boolean rotate(GameSquare[][] board, PieceSquare[][] rotatedPiece) {
        int boardHeight = board.length, boardWidth = board[0].length;
        // teame et langev klots on olemas

        int pieceHeight = rotatedPiece.length, pieceWidth = rotatedPiece[0].length;

        ArrayList<Integer> rowPresence = new ArrayList<>();
        ArrayList<Integer> colPresence = new ArrayList<>();
        // 1. otsime langeva klotsi keskpunkti praegusel laual
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (board[r][c].isOccupied()) {
                    rowPresence.add(r);
                    colPresence.add(c);
                }
            }
        }
        int rCoordinate = average(rowPresence) - Math.round(pieceHeight / 2);
        int cCoordinate = average(colPresence) - Math.round(pieceWidth / 2);
        
        // 2. proovime kas nendele koordinaatidele mahub klotsi asetama üldse
        try {
            for (int r = 0; r < pieceHeight; r++) {
                for (int c = 0; c < pieceWidth; c++) {
                    GameSquare s = board[r + rCoordinate][c + cCoordinate];
                    if (s.isFixed()) {
                        return false;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // ei olnud ruumi, ei tee midagi
            return false;
        }
        
        // 3. kui jõudsime siia siis kindlasti mahtus, kõigepealt laud puhtaks ja siis uus
        clearBoard(board);
        for (int r = 0; r < pieceHeight; r++) {
            for (int c = 0; c < pieceWidth; c++) {
                GameSquare s = board[r + rCoordinate][c + cCoordinate];
                if (rotatedPiece[r][c].isOccupied()) {
                    s.setOccupied();
                }
            }
        }
        // 4. edaspidiseks on currentPiece praegune rotatedPiece
        return true;

    }

    private static int average(ArrayList<Integer> array) {
        int sum = 0;
        for (int n : array) {
            sum += n;
        }
        return sum / array.size();
    }

    private static void clearBoard(GameSquare[][] board) {
        for (GameSquare[] row : board) {
            for (GameSquare square : row) {
                if (square.isOccupied()) {
                    square.setEmpty();
                }
            }
        }
    }
}
