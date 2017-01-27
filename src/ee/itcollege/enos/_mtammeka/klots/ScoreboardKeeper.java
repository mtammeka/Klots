package ee.itcollege.enos._mtammeka.klots;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Madis on 26.01.2017.
 */
public class ScoreboardKeeper {
    private static final int LEADERBOARD_SIZE = 10;

    private ArrayList<ScoreEntry> entries;

    ScoreboardKeeper() throws IOException {
        entries = new ArrayList<>();

        String S = File.separator;
        File file = new File("src" + S + "ee" + S + "itcollege" + S + "enos" + S + "_mtammeka" + S + "klots" + S + "scoreboard.txt");


        if (file.canRead()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {

                String[] nameAndScore = line.split("<splitter>");
                String name = nameAndScore[0];
                int score = Integer.parseInt(nameAndScore[1]);
                offerNewEntry(name, score);
                line = reader.readLine();
            }
            reader.close();
        }

    }

    String getScoreBoard() {
        //anname tagasi edetabeli
        StringBuilder printOut = new StringBuilder("\n");
        for (ScoreEntry entry : entries) {
            printOut.append(entry.getName());
            for (int i = entry.getName().length(); i < 20; i++) {
                // natuke toore tabeli formattimine, et skoorid joonduks paremale
                printOut.append(" ");
            }
            printOut.append(entry.getScore());
            printOut.append("\n");
        }
        return printOut.toString();

    }

    boolean offerNewEntry(String name, int score) {
        Collections.sort(entries);
        // leiame hetke kõrgeima skoori (TOP 10 seas)
        int lowest = Integer.MAX_VALUE, counter = 0;
        for (ScoreEntry entry : entries) {
            if (entry.getScore() < lowest) {
                lowest = entry.getScore();
            }
            counter++;
            if (counter == LEADERBOARD_SIZE) {
                // kui kogemata kombel on edetabelis üle 10 sissekande siis madalamad skoorid lõigatakse siin maha
                for (int i = LEADERBOARD_SIZE; i < entries.size(); i++) {
                    entries.remove(i);
                }
            }
        }

        if (score <= lowest && counter >= LEADERBOARD_SIZE) {
            return false; // pakutav skoor edetabelisse ei mahu
        } else {

            if (counter >= LEADERBOARD_SIZE) {
                // kui skoor mahuks edetabelisse aga 10 sissekannet juba olemas
                entries.remove(entries.size() - 1); // viimase eemaldamine
                entries.add(new ScoreEntry(name, score));
                Collections.sort(entries);
                return true; // jee lisasime skoori
            } else {
                // edetabelis ongi vähem kui 10 sissekannet
                entries.add(new ScoreEntry(name, score));
                Collections.sort(entries);
                return true; // jee lisasime skoori
            }
        }
    }

    void close() throws IOException {
        // entries kirjutatakse faili ära
        String S = File.separator;
        File file = new File("src" + S + "ee" + S + "itcollege" + S + "enos" + S + "_mtammeka" + S + "klots" + S + "scoreboard.txt");
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        StringBuilder formattedScoreBoard = new StringBuilder("");
        for (ScoreEntry entry : entries) {
            formattedScoreBoard.append(entry.getName());
            formattedScoreBoard.append("<splitter>");
            formattedScoreBoard.append(Integer.toString(entry.getScore()));
            formattedScoreBoard.append("\n");
        }

        writer.write(formattedScoreBoard.toString());
        writer.close();
    }

}

class ScoreEntry implements Comparable {
    private String name;
    private int score;
    ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }
    int getScore() {
        return score;
    }

    String getName() {
        return name;
    }


    @Override
    public int compareTo(Object anotherEntry) throws ClassCastException {
        if (!(anotherEntry instanceof ScoreEntry)) {
            throw new ClassCastException("Sisene viga: skoori võrreldi millegi muu kui teise skooriga.");
        }

        int otherScore = ((ScoreEntry) anotherEntry).getScore();
        return (-1) * (this.score - otherScore); // kui positiivne, siis on _see_ skoor parem
        // kui negatiivne on _see_skoor halvem
        // see võimaldab arraylisti sortimist kasutada
        // korrutame -1 -ga kuna suurem number peaks tulema algusesse
    }
}
