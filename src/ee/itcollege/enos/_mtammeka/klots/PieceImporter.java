package ee.itcollege.enos._mtammeka.klots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Madis on 15.01.2017.
 */
public class PieceImporter {
    private ArrayList<Piece> pieces;

    PieceImporter() throws IOException {
        File file = new File("src/ee/itcollege/enos/_mtammeka/Klots/pieces.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));

        ArrayList<String> rawPieceLines = new ArrayList<>();
        pieces = new ArrayList<>();

        String temp = reader.readLine();
        while (temp != null) {
            if (!temp.isEmpty() && temp.toCharArray()[0] == '#') {
                // kommenteeritud ridu ignoreeritakse
            } else {
                if (!temp.isEmpty()) {
                    /* http://stackoverflow.com/questions/12106757/removing-spaces-at-the-end-of-a-string-in-java
                     stringi lõpust tühikute eemaldamine (neid raskem pieces.txt koostades jälgida)
                     \\s on vist nagu whitespace, "+" on et üks või mitu, dollar tähendab et lõpus
                     */
                    rawPieceLines.add(temp.replaceAll("\\s+$", ""));
                } else if (!rawPieceLines.isEmpty()) {
                    pieces.add(new Piece(rawPieceLines));
                    rawPieceLines = new ArrayList<>();
                }
            }
            temp = reader.readLine();
        }

        reader.close();

        // ee siin praegu testime kas midagi sai valmis
/*        for (Piece p : pieces) {
            PieceSquare[][] piece = p.getPiece();
            for (int i = 0; i < 4; i++) {
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
                System.out.println();
                piece = Piece.getNextRotation(piece);
            }

        } *///
        // pieces.get(0) kuni pieces.get(pieces.size() - 1) on kõik loetud palad
        // Piece.getNextRotation -iga saab neid roteerida
        // kuskil peaks meeles hoidma currentpiecei
        // arraylisti pieces peaks eastama BoardHandlerisse vist


    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }
}
