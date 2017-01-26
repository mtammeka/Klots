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
        String S = File.separator;
        File file = new File("src" + S + "ee" + S + "itcollege" + S + "enos" + S + "_mtammeka" + S + "klots" + S + "pieces.txt");

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
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }
}
