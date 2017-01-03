package ee.itcollege.enos._mtammeka.klots;

/**
 * Created by Madis on 03.01.2017.
 */
public class TetrisBoard {

    private boolean fallingPieceExists = false;
    private boolean gameOver = false;

    private int score;

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


    public TetrisBoard(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        initiateBoard();

    }

    private int[][] board = new int[rows][columns];

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
    
    public int[][] getBoard() {
        return board;
    }
    
    public void advanceTheGame() {
        if (fallingPieceExists) {
            advanceAPiece(board);
        }

        //
        //
        //
        //
        //
        //
        //
        //
        /* siin meetodis peaks ka kasutaja sisenditega tegelema
         * samuti võimalusega et mäng on läbi
          * */
    }




    public boolean isGameOver() {
        return gameOver;
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
            pieceType = (int) ((int) (Math.random() * 19) + 1); //nr 20 ongi "juhuslik", 1..19 on konkreetsed palad
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
            /* pala ei mahtunud väljale ära*/
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

}

