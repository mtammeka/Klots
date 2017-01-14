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
            lines.add(temp);
            System.out.println(lines.get(lines.size() - 1));

            temp = reader.readLine();
        }

        reader.close();
    }
}
