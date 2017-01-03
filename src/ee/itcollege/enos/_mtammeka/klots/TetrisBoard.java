package ee.itcollege.enos._mtammeka.klots;

import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Madis on 03.01.2017.
 */
public class TetrisBoard {

    private boolean fallingPieceExists = false;
    private boolean gameOver = false;
    private int score = 0;
    private int rows, columns;
    private final static int EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2, FIXED_SQUARE = 3;
    private final static int   UP_L = 1, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4,
                                UP_TRANS_L = 5, DOWN_TRANS_L = 6, LEFT_TRANS_L = 7, RIGHT_TRANS_L = 8,
                                CUBE = 9,
                                UP_T = 10, LEFT_T = 11, RIGHT_T = 12, DOWN_T = 13,
                                UP_DOWN_S = 14, LEFT_RIGHT_S = 15,
                                UP_DOWN_TRANS_S = 16, LEFT_RIGHT_TRANS_S = 17,
                                DOWN_BAR = 18, UP_BAR = 19,
                                RANDOM = 20;
    private int globalCurrentPieceType;
    private final static byte DOWN_ARROW = 1, LEFT_ARROW = 2, RIGHT_ARROW = 3, UP_ARROW = 4;
    private int[][] board;

    public TetrisBoard(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        this.board = new int[rows][columns];

        initiateBoard();
    }

    public int[][] getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }
    
    public void advanceTheGame(Set<Byte> commandQueue) {
        if (fallingPieceExists) {

            try {
                applyUserInput(board, commandQueue);
            } catch (ConcurrentModificationException exception) {
                // teehehee ei oska midagi ette võtta sellega
                // mäng töötab
            }

            advanceAPiece(board);

            score += removeCompletedLines(board);
        } else {
            gameOver = !spawnAPiece(board, 0, columns / 2, RANDOM);
        }

    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void initiateBoard() {
        // Siia satume kui mängu just alustatakse
        // rows ja columns - laua suurus on konstruktoris seatud
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = EMPTY_SQUARE;
            }
        }

        // laud on kindlasti tühi kuna kutsuti initiateBoard

        // spawnAPiece annab boolean väärtuse eduka soorituse kohta aga praegu seda kasutama ei pea
        spawnAPiece(board, 0, columns / 2, RANDOM);

        // initiateBoard seest kutsutud spawnAPiece ei saa ebaõnnestuda
        fallingPieceExists = true;
    }

    private boolean spawnAPiece(int[][] targetBoard, final int ROW_OFFSET, final int COL_OFFSET, int pieceType) {
        int[][] upL =       { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XO
                            {OCCUPIED_SQUARE, EMPTY_SQUARE},                        // XO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // XX

        int[][] downL =     { {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                   // XX
                            {EMPTY_SQUARE, OCCUPIED_SQUARE},                        // OX
                            {EMPTY_SQUARE, OCCUPIED_SQUARE} };                      // OX

        int[][] leftL =     { {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE},  // XXX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE} };        // XOO

        int[][] rightL =    { {EMPTY_SQUARE, EMPTY_SQUARE, OCCUPIED_SQUARE},        // OOX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE} };  // XXX

        int[][] upTransL =  { {EMPTY_SQUARE, OCCUPIED_SQUARE},                      // OX
                            {EMPTY_SQUARE, OCCUPIED_SQUARE},                        // OX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // XX


        int[][] downTransL = { {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                  // XX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE},                        // XO
                            {OCCUPIED_SQUARE, EMPTY_SQUARE} };                      // XO

        int[][] leftTransL = { {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE}, // XXX
                            {EMPTY_SQUARE, EMPTY_SQUARE, OCCUPIED_SQUARE} };        // 00X

        int[][] rightTransL = { {OCCUPIED_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE},      // X00
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE}};   // XXX


        int[][] cube =      { {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                   // XX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // XX

        int[][] upT =       { {EMPTY_SQUARE, OCCUPIED_SQUARE, EMPTY_SQUARE},        // OXO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE }};  // XXX

        int[][] leftT =     { {EMPTY_SQUARE, OCCUPIED_SQUARE},                      // OX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                     // XX
                            {EMPTY_SQUARE, OCCUPIED_SQUARE} };                      // OX

        int[][] rightT =    { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                     // XX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE} };                      // XO

        int[][] downT =     { {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE},  // XXX
                            {EMPTY_SQUARE, OCCUPIED_SQUARE, EMPTY_SQUARE} };        // OXO

        int[][] upDownS =   { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                     // XX
                            {EMPTY_SQUARE, OCCUPIED_SQUARE} };                      // OX

        int[][] leftRightS = { {EMPTY_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE},    // OXX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE, EMPTY_SQUARE} };     // XXO

        int[][] upDownTransS = { {EMPTY_SQUARE, OCCUPIED_SQUARE},                   // OX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                     // XX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE} };                      // XO

        int[][] leftRightTransS = { {OCCUPIED_SQUARE, OCCUPIED_SQUARE, EMPTY_SQUARE},// XXO
                            {EMPTY_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE} };     // OXX

        int[][] downBar =   { {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE} };

        int[][] upBar =     { {OCCUPIED_SQUARE},
                            {OCCUPIED_SQUARE},
                            {OCCUPIED_SQUARE},
                            {OCCUPIED_SQUARE} };


        int[][] currentPiece = cube; // muutujale tuleb mingi algväärtus anda

        // edaspidi loodaks sama meetodit kasutada ka "roteerimisel," e langeva pala väljavahetamisel
        // selle jaoks on vaja eelnevast palast vastav koht puhtaks teha. Siin on veel tegemist
        // ... praegu tegime kustutamiseks eraldi meetodi, siit kommenteerime välja
        /*for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (targetBoard[i + ROW_OFFSET][j + COL_OFFSET] != FIXED_SQUARE) {
                    targetBoard[i + ROW_OFFSET][j + COL_OFFSET] = EMPTY_SQUARE;
                } else {
                    gameOver = true;
                }
            }
        }*/

        if (pieceType == RANDOM) {
            pieceType = ((int) (Math.random() * 19) + 1); //nr 20 ongi "juhuslik", 1..19 on konkreetsed palad
        }

        globalCurrentPieceType = pieceType; // ei tea kas seda on vaja

        // jälle pikk kood - kui kunagi aega, sooviks asja elegantselt lahendada... HashMap-iga?
        switch (pieceType) {
            case UP_L:      currentPiece = upL;
                break;
            case DOWN_L:    currentPiece = downL;
                break;
            case RIGHT_L:   currentPiece = rightL;
                break;
            case LEFT_L:    currentPiece = leftL;
                break;
            case UP_TRANS_L: currentPiece = upTransL;
                break;
            case DOWN_TRANS_L: currentPiece = downTransL;
                break;
            case LEFT_TRANS_L: currentPiece = leftTransL;
                break;
            case RIGHT_TRANS_L: currentPiece = rightTransL;
                break;
            case CUBE:      currentPiece = cube;
                break;
            case UP_T:      currentPiece = upT;
                break;
            case LEFT_T:    currentPiece = leftT;
                break;
            case RIGHT_T:   currentPiece = rightT;
                break;
            case DOWN_T:    currentPiece = downT;
                break;
            case UP_DOWN_S: currentPiece = upDownS;
                break;
            case LEFT_RIGHT_S: currentPiece = leftRightS;
                break;
            case UP_DOWN_TRANS_S: currentPiece = upDownTransS;
                break;
            case LEFT_RIGHT_TRANS_S: currentPiece = leftRightTransS;
                break;
            case DOWN_BAR:  currentPiece = downBar;
                break;
            case UP_BAR:    currentPiece = upBar;
                break;

        }

        // siin peaks ArrayCopy meetodit kasutama? kas ikka julgeb?
        int[][] tempBoard = new int[rows][columns];
        try {


            for (int i = 0; i < targetBoard.length; i++) {
                for (int j = 0; j < targetBoard[i].length; j++) {
                    tempBoard[i][j] = targetBoard[i][j];
                }

            }
            for (int i = 0; i < currentPiece.length; i++) {
                for (int j = 0; j < currentPiece[i].length; j++) {
                    if (tempBoard[i + ROW_OFFSET][j + COL_OFFSET] == EMPTY_SQUARE) {
                        tempBoard[i + ROW_OFFSET][j + COL_OFFSET] = currentPiece[i][j];
                    } else {
                        // palale jäi ette fikseeritud ruut
                        return false;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            /* pala ei mahtunud väljale ära
            see tähendab ka et mäng on läbi (gameOver = true)
             */
            return false;
        }
        // "telgitagusel" mängulaual on nüüd küll pala paigas
        for (int i = 0; i < targetBoard.length; i++) {
            for (int j = 0; j < targetBoard[i].length; j++) {
                targetBoard[i][j] = tempBoard[i][j];
            }

        }
        fallingPieceExists = true;
        return true;
    }
    private void advanceAPiece(int[][] theBoard) {
        int nextSquareDown = FIXED_SQUARE; // initialise to "the edge of the board"

        for (int j = 0; j < theBoard[0].length; j++) { // alustame esimese tulba peal
            for (int i = theBoard.length - 1; i >= 0; i--) { // alustame tulba alumisest ruudust, e viimasel real
                if (theBoard[i][j] == OCCUPIED_SQUARE && nextSquareDown == FIXED_SQUARE) {
                    for (int k = 0; k < theBoard.length; k++) {
                        for (int l = 0; l < theBoard[k].length; l++) {
                            if (theBoard[k][l] == OCCUPIED_SQUARE)
                                theBoard[k][l] = FIXED_SQUARE;
                        }
                    }
                    fallingPieceExists = false;
                    return; // on selge et midagi edasi nihutada pole
                }
                nextSquareDown = theBoard[i][j];
            }
            nextSquareDown = FIXED_SQUARE;
        }

        // siia jõuame vaid siis, kui kindlasti on ruumi ühe sammu võrra edasi nihutamiseks
        int occupiedCountOnThisRow = 0;
        for (int j = 0; j < theBoard[0].length; j++) { // alustame esimese tulba peal
            for (int i = theBoard.length - 1; i >= 0; i--) { // alustame tulba kõige alumise ruudu peal
                if (theBoard[i][j] == OCCUPIED_SQUARE) {
                    occupiedCountOnThisRow++;

                    // nihutatavate klotside hulk on loetud kui jõuame tühja VÕI fikseeritud ruuduni
                    // (lateraalliikumine võimaldab mängijal pala lükata mõne fikseeritud ruudu alla!)
                    if (i == 0 || (theBoard[i - 1][j] == EMPTY_SQUARE || theBoard[i - 1][j] == FIXED_SQUARE)) {
                        theBoard[i][j] = EMPTY_SQUARE;
                        theBoard[i + occupiedCountOnThisRow][j] = OCCUPIED_SQUARE;
                        // selles tulbas peaks kõik nihutatud olema mida nihutada vaja
                        break;
                    }
                }
            }
            occupiedCountOnThisRow = 0;
        }
    }
    private void applyUserInput(int[][] theBoard, Set<Byte> commandQueue) {

        while (!commandQueue.isEmpty()) {

            for (byte thisKeyStroke : commandQueue) {
                switch (thisKeyStroke) {
                    case LEFT_ARROW:
                        moveAPieceLeft(theBoard);
                        commandQueue.remove(LEFT_ARROW);
                        break;
                    case RIGHT_ARROW:
                        moveAPieceRight(theBoard);
                        commandQueue.remove(RIGHT_ARROW);
                        break;
                    case DOWN_ARROW:
                        advanceAPiece(theBoard); // ei tea kas on nii lihtne
                        commandQueue.remove(DOWN_ARROW);
                        break;
                    case UP_ARROW:
                        // ok nii et roteerimine
                        // -- vaatame mis on globalCurrentPieceType
                        // -- otsime nt praeguse klotsi kõige ülemisel real kõige vasakpoolsema ruudu
                        // -- uue pala joonistamise koordinaat (spawnAPiece(.., rida, tulp, ..) saadakse
                        // -- sellele koordinaadile pala tüübile ja soovitud rotatsioonile vastavat sammu liites
                        // -- (milline samm mille jaoks on vajalik tuleb eraldi välja vaadata vist)
                        // -- siis 1.) testijoonistamine (kas on ruumi?) 2.) vana pala kustutamine 3.) reaalne
                        // -- uue joonistamine
                        tryRotateCurrentPiece(theBoard);
                        commandQueue.remove(UP_ARROW);
                        break;
                }
            }
        }
    }

    private void moveAPieceLeft(int[][] theBoard) {
        int nextSquareLeft = FIXED_SQUARE;
        // for (int i = 0; i < theBoard.length; i++) { // alustame esimesel real
        // sama mis nested for tsüklid - tegelikult pole hea valik kuna segab ridadest-tulpadest mõtlemist
        for (int[] thisBoard : theBoard) {
            for (int j = 0; j < thisBoard.length; j++) { // alustame 1. tulbast e vasakpoolseimast ruudust
                if (thisBoard[j] == OCCUPIED_SQUARE && nextSquareLeft == FIXED_SQUARE) {
                    return; // vähemalt üks hõivatud ruut on kohe serva/fikseeritud ruudu kõrval
                    // seega nihutada ei saa
                }
                nextSquareLeft = thisBoard[j];
            }
            nextSquareLeft = FIXED_SQUARE; // jälle alustame servalt - sama hea kui fikseeritud ruudu kõrvalt
        }

        // jõudsime siia, järelikult on ruumi et 1 sammu võrra vasakule nihutada
        int occupiedCountOnThisRow = 0;
        for (int i = 0; i < theBoard.length; i++) { // liigume ülemiselt realt alla
            for (int j = theBoard[i].length - 1; j >= 0; j--) { // alustame parempoolseimast ruudust
                if (theBoard[i][j] == OCCUPIED_SQUARE) {
                    occupiedCountOnThisRow++;
                } else if (theBoard[i][j] == EMPTY_SQUARE && occupiedCountOnThisRow != 0) {
                    theBoard[i][j] = OCCUPIED_SQUARE;
                    theBoard[i][j + occupiedCountOnThisRow] = EMPTY_SQUARE;
                    occupiedCountOnThisRow = 0;
                }
            }
        }
    }

    private void moveAPieceRight(int[][] theBoard) {
        int nextSquareRight = FIXED_SQUARE;
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = theBoard[i].length - 1; j >= 0; j--) {
                if (theBoard[i][j] == OCCUPIED_SQUARE && nextSquareRight == FIXED_SQUARE) {
                    return;
                }
                nextSquareRight = theBoard[i][j];
            }
            nextSquareRight = FIXED_SQUARE;
        }

        // ruumi jätkub üheks sammuks vähemalt
        int occupiedCountOnThisRow = 0;
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[i].length; j++) {
                if (theBoard[i][j] == OCCUPIED_SQUARE) {
                    occupiedCountOnThisRow++;
                } else if (theBoard[i][j] == EMPTY_SQUARE && occupiedCountOnThisRow != 0) {
                    theBoard[i][j] = OCCUPIED_SQUARE;
                    theBoard[i][j - occupiedCountOnThisRow] = EMPTY_SQUARE;
                    occupiedCountOnThisRow = 0;
                }
            }
        }
    }

    private void tryRotateCurrentPiece(int[][] theBoard) {
        boolean foundSomething = false;
        boolean pieceFitSuccessful = false;
        int x = 0, y = 0;
        FIND_FIRST_OCCUPIED_SQUARE:
        for(x = 0; x < theBoard.length; x++) {
            for (y = 0; y < theBoard[x].length; y++) {
                if (theBoard[x][y] == OCCUPIED_SQUARE) {
                    foundSomething = true;
                    break FIND_FIRST_OCCUPIED_SQUARE;
                }

            }
        }

        if (!foundSomething) {
            System.out.println("mingil moel ei leidnud ühtegi pala");
            return;
        }

        // siin võiks teha ajutise laua, kopeerida kogu praeguse sisu
        int[][] temporaryTheBoard = new int[rows][columns];
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[i].length; j++) {
                temporaryTheBoard[i][j] = theBoard[i][j];
            }
        }

        if (globalCurrentPieceType != CUBE) {
            wipePiece(temporaryTheBoard);
        } else {
            return;
        }

        switch (globalCurrentPieceType) {
            case UP_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, LEFT_L);
                break;
            case DOWN_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, RIGHT_L);
                break;
            case RIGHT_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, UP_L);
                break;
            case LEFT_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, DOWN_L);
                break;
            case UP_TRANS_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, RIGHT_TRANS_L);
                break;
            case DOWN_TRANS_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, LEFT_TRANS_L);
                break;
            case LEFT_TRANS_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, UP_TRANS_L);
                break;
            case RIGHT_TRANS_L:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, DOWN_TRANS_L);
                break;
            case UP_T:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, RIGHT_T);
                break;
            case LEFT_T:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, UP_T);
                break;
            case RIGHT_T:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, DOWN_T);
                break;
            case DOWN_T:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, LEFT_T);
                break;
            case UP_DOWN_S:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, LEFT_RIGHT_S);
                break;
            case LEFT_RIGHT_S:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, UP_DOWN_S);
                break;
            case UP_DOWN_TRANS_S:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, LEFT_RIGHT_TRANS_S);
                break;
            case LEFT_RIGHT_TRANS_S:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, UP_DOWN_TRANS_S);
                break;
            case DOWN_BAR:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, UP_BAR);
                break;
            case UP_BAR:
                pieceFitSuccessful = spawnAPiece(temporaryTheBoard, x, y, DOWN_BAR);
                break;
        }

        if (pieceFitSuccessful) {
            for (int i = 0; i < theBoard.length; i++) {
                for (int j = 0; j < theBoard[i].length; j++) {
                    theBoard[i][j] = temporaryTheBoard[i][j] ;
                }
            }
        } else {
            System.out.println("roteerimiseks pole ruumi");
        }

    }

    private static void wipePiece(int[][] theBoard) {
        // ilmselt ajutine meetod pala mugavaks eemaldamiseks
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[i].length; j++) {
                if (theBoard[i][j] == OCCUPIED_SQUARE) {
                    theBoard[i][j] = EMPTY_SQUARE;
                }
            }

        }
    }

    private int removeCompletedLines(int[][] theBoard) {

        LinkedHashSet<Integer> completedLines = new LinkedHashSet<>();

        for (int i = 0; i < theBoard.length; i++) { // rida-haaval

            for (int j = 0; j < theBoard[i].length; j++) {
                if (theBoard[i][j] != FIXED_SQUARE) {
                    break;
                } else if ((theBoard[i].length - 1) == j) {
                    // jõudsime siia - täitunud rida on leitud
                    completedLines.add(i);
                }
            }
        }

        if (!completedLines.isEmpty()) {
            // jõudsime siia - read mis tuleb eemaldada on loetud, vähemalt üks on olemas

            for (int i = 0; i < theBoard[0].length; i++) {
                // siin tegeletakse ühe tulbaga korraga
                int[] temporaryColumn = new int[theBoard.length];

                // koopia ilma "valmis" ridadeta, ilma mängupalata
                for (int j = theBoard.length - 1, k = j; k >= 0; j--, k--) {
                    if (completedLines.contains(j)) {
                        k++;
                    } else if (j < 0) {
                        temporaryColumn[k] = EMPTY_SQUARE;
                    } else if (theBoard[j][i] == OCCUPIED_SQUARE) {
                        // mängupala praegu ei lisata, sest see nihkuks koos fikseeritud ruutudega
                        temporaryColumn[k] = EMPTY_SQUARE;
                    } else {
                        temporaryColumn[k] = theBoard[j][i];
                    }
                    // mõlemat käiakse läbi "tagurpidi,"
                    // nii et koopia jääb "õiget" pidi hahaha
                }

                // mängupala lisatakse algse positsiooniga
                for (int j = theBoard.length - 1; j >= 0; j--) {
                    if (theBoard[j][i] == OCCUPIED_SQUARE) {
                        // siin loodetavasti kopeeritakse liikuv pala sama asukohaga
                        // ... tegelikult SUHTELISELT kindlalt praegu ei olegi liikuvat pala aga...
                        temporaryColumn[j] = OCCUPIED_SQUARE;
                    }
                }

                // algne tulp asendatakse tulbaga, kust "valmis" read on eemaldatud, pala on endises asukohas
                for (int j = theBoard.length - 1; j >= 0; j--) {
                    theBoard[j][i] = temporaryColumn[j];
                }
            }

        }

        return completedLines.size();
    }

}