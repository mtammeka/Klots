package ee.itcollege.enos._mtammeka.klots;

import java.util.LinkedHashSet;

/**
 * Created by Madis on 15.01.2017.
 */
public class ScoreCounter {
    public static int removeLines(GameSquare[][] board) {
        int boardHeight = board.length, boardWidth = board[0].length;
        LinkedHashSet<Integer> completedLines = new LinkedHashSet<>();

        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (!board[r][c].isFixed()) {
                    break;
                } else if ((boardWidth - 1) == c) {
                    // jõudsime siia - täitunud rida on leitud
                    completedLines.add(r);
                }
            }
        }

        // jõudsime siia - read mis tuleb eemaldada on leitud

        System.out.println(completedLines + " siit edasi");

        // käib alt üles board massiivi läbi ja jätab "kopeerimata" read mis completedLines arrays mainitud
        // võib arvestada et "Occupied" ei ole hetkel midagi laual
        return completedLines.size();
    }
}
