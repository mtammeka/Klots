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

        for (int c = boardWidth - 1; c >= 0; c--) { // tulphaaval
            PieceSquare[] tempColumn = new PieceSquare[boardHeight];
            int n = boardHeight - 1;
            for (int r = n; r >= 0; r--) { // alustab alumiselt realt

                if (completedLines.contains(r)) {
                    continue;
                }
                tempColumn[n] = new PieceSquare();
                copyState(board[r][c], tempColumn[n]);
                n--;
            }
            for (; n >= 0; n--) {
                tempColumn[n] = new PieceSquare();
                tempColumn[n].setEmpty();
            }
            for (int r = 0; r < boardHeight; r++) {
                copyState(tempColumn[r], board[r][c]);
            }
        }

        return completedLines.size();
    }


    // kuskil oli veel koht kus sellist ägedat interfacei meetodit kasutada saab, otsin pärast üles
    public static void copyState(TetrisSquare from, TetrisSquare to) {
        if (from.isFixed()) {
            to.setFixed();
        } else if (from.isOccupied()) {
            to.setOccupied();
        } else {
            to.setEmpty();
        }
    }

}
