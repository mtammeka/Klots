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

        int leftEdge = 0, rightEdge = 0, lowEdge = 0, highEdge = 0;
        ArrayList<Integer> rowPresence = new ArrayList<>();
        ArrayList<Integer> colPresence = new ArrayList<>();
        // 1. otsime langeva klotsi keskpunkti praegusel laual
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (board[r][c].isOccupied()) {
                    if (highEdge == 0) {
                        highEdge = r;
                    } else if (lowEdge < r) {
                        lowEdge = r;
                    }

                    if (leftEdge == 0) {
                        leftEdge = c;
                    } else if (rightEdge < c) {
                        rightEdge = c;
                    }

                }
            }
        }
        int cCoordinate = Math.round(((leftEdge + rightEdge) / 2) - (pieceWidth / 2));
        int rCoordinate = Math.round(((highEdge + lowEdge) / 2) - (pieceHeight / 2));

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
