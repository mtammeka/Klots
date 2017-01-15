package ee.itcollege.enos._mtammeka.klots;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Madis on 15.01.2017.
 */
public class Piece {

    GameSquare[][] piece;

    Piece(ArrayList<String> rawPieceLines) throws IOException {
        int maxRows = 0, maxColumns = 0;
        /* saame tüki toorel tekstikujul, nt
          * X
          * X
          * XX
          * sellest tehakse siin veel 3 roteeritud varianti*/
        maxRows = rawPieceLines.size();
        for (String s : rawPieceLines) {
            if (s.length() > maxColumns) {
                maxColumns = s.length();
            }
        }
        System.out.println("---");
        System.out.println("R: " + maxRows + " C: " + maxColumns);
        piece = new GameSquare[maxRows][maxColumns];
        for (int row = 0; row < maxRows; row++) {
            for (int column = 0; column < maxColumns; column++) {
                piece[row][column] = new GameSquare(row, column);
            }

            String thisRow = rawPieceLines.get(row);
            for (int column = 0; column < thisRow.length(); column++) {
                if (thisRow.toCharArray()[column] == 'X') {
                    piece[row][column].setOccupied();
                }
            }
        }

        // ee siin praegu testima kas midagi sai valmis
        for (int row = 0; row < piece.length; row++) {
            for (int column = 0; column < piece[0].length; column++) {
                if (piece[row][column].isOccupied()) {
                    System.out.printf("%c", 'X');
                } else {
                    System.out.printf("%c", ' ');
                }
            }
            System.out.println();
        } // jep prindib samad palad tagasi

    }
}
