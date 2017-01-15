package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 15.01.2017.
 */
public class PieceShifter {
    public static boolean shiftRight(GameSquare[][] board) {
        boolean isNextSquareRightFixed = true;
        int boardHeight = board.length;
        int boardWidth = board[0].length;
        for (int r = 0; r < boardHeight; r++) {
            for (int c = boardWidth - 1; c >= 0; c--) {
                if (board[r][c].isOccupied() && isNextSquareRightFixed) {
                    return false;
                }
                isNextSquareRightFixed = board[r][c].isFixed();
            }
            isNextSquareRightFixed = true;
        }
        // ruumi jätkub üheks sammuks vähemalt
        int occupiedCountOnThisRow = 0;
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (board[r][c].isOccupied()) {
                    occupiedCountOnThisRow++;
                } else if (board[r][c].isEmpty() && occupiedCountOnThisRow != 0) {
                    board[r][c].setOccupied();
                    board[r][c - occupiedCountOnThisRow].setEmpty();
                    occupiedCountOnThisRow = 0;
                }
            }
        }
        return true;
    }

    public static boolean shiftLeft(GameSquare[][] board) {
        boolean isNextSquareLeftFixed = true;
        int boardHeight = board.length;
        int boardWidth = board[0].length;

        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (board[r][c].isOccupied() && isNextSquareLeftFixed) {
                    return false;
                }
                isNextSquareLeftFixed = board[r][c].isFixed();
            }
            isNextSquareLeftFixed = true;
        }

        // jõudsime siia, järelikult on ruumi et 1 sammu võrra vasakule nihutada
        int occupiedCountOnThisRow = 0;
        for (int r = 0; r < boardHeight; r++) {
            for (int c = boardWidth - 1; c >= 0; c--) {
                // alustame parempoolseimast ruudust
                if (board[r][c].isOccupied()) {
                    occupiedCountOnThisRow++;
                } else if (board[r][c].isEmpty() && occupiedCountOnThisRow != 0) {
                    board[r][c].setOccupied();
                    board[r][c + occupiedCountOnThisRow].setEmpty();
                    occupiedCountOnThisRow = 0;
                }
            }
        }
        return true;
    }
}
