package ee.itcollege.enos._mtammeka.klots;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Madis on 15.01.2017.
 */
public class Piece {

    private PieceSquare[][] piece;

    Piece(ArrayList<String> rawPieceLines) throws IOException {
        int maxRows = 0, maxColumns = 0;
        /* saame tüki toorel tekstikujul, nt
          * X
          * X
          * XX
          * */
        maxRows = rawPieceLines.size();
        for (String s : rawPieceLines) {
            if (s.length() > maxColumns) {
                maxColumns = s.length();
            }
        }
        
        piece = new PieceSquare[maxRows][maxColumns];
        for (int row = 0; row < maxRows; row++) {
            for (int column = 0; column < maxColumns; column++) {
                piece[row][column] = new PieceSquare();
            }

            String thisRow = rawPieceLines.get(row);
            for (int column = 0; column < thisRow.length(); column++) {
                if (thisRow.toCharArray()[column] == 'X') {
                    piece[row][column].setOccupied();
                }
            }
        }
    }

    PieceSquare[][] getPiece() {
        return piece;
    }

    static PieceSquare[][] getNextRotation(final PieceSquare[][] inputPiece) {

        int newRows = inputPiece[0].length;
        int newColumns = inputPiece.length;

        PieceSquare[][] rotatedPiece = new PieceSquare[newRows][newColumns];
        int r = 0, c = 0;
        for (int rows = 0; rows < newRows; rows++) {
            for (int columns = newColumns - 1; columns >= 0; columns--) {
                rotatedPiece[rows][columns] = inputPiece[r][c];
                r++;
            }
            r = 0;
            c++;
        }
        return rotatedPiece;
    }

}
