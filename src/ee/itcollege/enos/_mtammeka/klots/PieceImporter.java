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

    PieceImporter() throws IOException {
        File file = new File("src/ee/itcollege/enos/_mtammeka/Klots/pieces.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));

        ArrayList<String> lines = new ArrayList<>();

        String temp = reader.readLine();
        while (temp != null) {
            if (!temp.isEmpty() && temp.toCharArray()[0] == '#') {
                // kommenteeritud ridu ignoreeritakse
                temp = reader.readLine();
            } else {

                lines.add(temp);
                temp = reader.readLine();
            }
        }


        System.out.println(lines);

        reader.close();
    }
}
