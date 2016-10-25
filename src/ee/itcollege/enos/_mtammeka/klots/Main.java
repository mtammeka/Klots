package ee.itcollege.enos._mtammeka.klots;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    // TODO muutujate üle vaadata, palju ebavajalikku on siin ilmselt
    private final static int UP_L = 1, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4,
                            UP_TRANS_L = 5, DOWN_TRANS_L = 6, LEFT_TRANS_L = 7, RIGHT_TRANS_L = 8,
                            CUBE = 9,
                            UP_T = 10, LEFT_T = 11, RIGHT_T = 12, DOWN_T = 13,
                            UP_DOWN_S = 14, LEFT_RIGHT_S = 15,
                            UP_DOWN_TRANS_S = 16, LEFT_RIGHT_TRANS_S = 17,
                            DOWN_BAR = 18, UP_BAR = 19,
                            RANDOM = 20;

    private final static int EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2, FIXED_SQUARE = 3;
    private final static int SCENE_WIDTH = 400;
    private final static int SCENE_HEIGHT = 600;
    private final static int STEP = 20;
    private final static int ROWS = SCENE_HEIGHT / STEP;
    private final static int COLUMNS = SCENE_WIDTH / STEP;
    private long lastTimeStamp = 0;
    private static boolean fallingPieceExists = false;
    private static boolean gameOver = false; // kui tüki tekitamiseks pole ruumi... spawnAPiece ise kontrollib?
    private static ArrayList<Byte> commandQueue = new ArrayList<>();
    private final static byte DOWN_ARROW = 1, LEFT_ARROW = 2, RIGHT_ARROW = 3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        /*Scene constructor'i argumendid - parent root, x, y, fill color*/
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT, Color.CYAN);
        primaryStage.setScene(scene);

        /*Mängulaua nähtavatele ruutedele vastavate objektide eksemplaride loomine*/
        Rectangle[][] boardFX = new Rectangle[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                boardFX[i][j] = new Rectangle();
                boardFX[i][j].setX(j * STEP);
                boardFX[i][j].setY(i * STEP);
                boardFX[i][j].setWidth(STEP);
                boardFX[i][j].setHeight(STEP);
                boardFX[i][j].setStroke(Color.LIGHTGREEN);
                boardFX[i][j].setStrokeWidth(2);
                boardFX[i][j].setFill(Color.DARKGRAY);
                pane.getChildren().add(boardFX[i][j]);
            }
        }
        System.out.printf("COLUMNS: %d, ROWS: %d.\n", COLUMNS, ROWS);
        // Orienteerumise näited:
        //boardFX[14][0].setFill(Color.BLUE); // rida 13 tulp 1 - siniseks
        //boardFX[ROWS - 1][COLUMNS - 1].setFill(Color.YELLOW); // kõige alumine-parempoolne ruut kollaseks

        /* Luuakse "telgitagune" mängulaud, mängulaua ruutudel on kolm võimalikku seisundit:
         * EMPTY, OCCUPIED, FIXED */
        int[][] board = new int[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = EMPTY_SQUARE;
            }
        }

        // Anonüümne eksemplar AnimationTimer objektist - teeb võimalikuks, et ekraanil toimub midagi ajas
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Mängupala liiguks muidu liiga kiiresti
                if (now - lastTimeStamp > (Math.pow(10, 9)) / 20) { // now on nanosekundites
                    lastTimeStamp = now;

                    if (!fallingPieceExists) {
                        spawnAPiece(board, 0, COLUMNS / 2, RANDOM);
                    } else {
                        advanceAPiece(board);
                        applyUserInput(board);
                    }

                    // "Telgitaguse" laua põhjal muudetakse vajadusel nähtavaid ruute
                    carryBoardToBoardFX(board, boardFX, ROWS, COLUMNS);

                    if (gameOver) {
                        System.out.println("Game over");
                        Platform.exit();
                        System.exit(0);
                    }

                }
            }
        };

        // Puhver sisendkäskude jaoks tundus alguses hea mõte aga ilmselt mingi hetk tuleb lihtsamaks muuta
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            switch (key.getCode()) {
                case LEFT:
                    commandQueue.add(LEFT_ARROW);
                    break;
                case RIGHT:
                    commandQueue.add(RIGHT_ARROW);
                    break;
                case DOWN:
                    commandQueue.add(DOWN_ARROW);
                    break;
            }
        });

        timer.start();
        primaryStage.show();

    }

    private static void carryBoardToBoardFX(int[][] inputBoard, Rectangle[][] outputBoardFX, int ROW, int COL) {
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                switch (inputBoard[i][j]) {
                    case OCCUPIED_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.GREEN)
                            outputBoardFX[i][j].setFill(Color.GREEN);
                        break;
                    case FIXED_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.VIOLET)
                            outputBoardFX[i][j].setFill(Color.VIOLET);
                        break;
                    case EMPTY_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.DARKGRAY)
                            outputBoardFX[i][j].setFill(Color.DARKGRAY);
                        break;
                }
    }

    private static void spawnAPiece(int[][] targetBoard, final int ROW_OFFSET, final int COL_OFFSET, int pieceType) {
        int[][] upL =       { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XO
                            {OCCUPIED_SQUARE, EMPTY_SQUARE},                        // XO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // XX

        int[][] downL =     { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE},                        // OX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // OX

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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (targetBoard[i + ROW_OFFSET][j + COL_OFFSET] != FIXED_SQUARE) {
                    targetBoard[i + ROW_OFFSET][j + COL_OFFSET] = EMPTY_SQUARE;
                } else {
                    gameOver = true;
                }
            }
        }

        if (pieceType == RANDOM)
            pieceType = (int) (Math.random() * 19) + 1; //nr 20 ongi "juhuslik", 1..19 on konkreetsed palad

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

        }

        // siin peaks ArrayCopy meetodit kasutama? kas ikka julgeb?
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[i].length; j++) {
                targetBoard[i + ROW_OFFSET][j + COL_OFFSET] = currentPiece[i][j];
            }
        }
        // "telgitagusel" mängulaual on nüüd küll pala paigas
        fallingPieceExists = true;
    }

    private static void advanceAPiece(int[][] theBoard) {
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

    private static void applyUserInput(int[][] theBoard) {
        if (!commandQueue.isEmpty()) {
            switch (commandQueue.remove(0)) {
                case LEFT_ARROW:
                    System.out.println("official moving action");
                    moveAPieceLeft(theBoard);
                    break;
                case RIGHT_ARROW:
                    System.out.println("right moving action");
                    moveAPieceRight(theBoard);
                    break;
            }
        }
    }

    private static void moveAPieceLeft(int[][] theBoard) {
        int nextSquareLeft = FIXED_SQUARE;
        for (int i = 0; i < theBoard.length; i++) { // alustame esimesel real
            for (int j = 0; j < theBoard[i].length; j++) { // alustame 1. tulbast e vasakpoolseimast ruudust
                if (theBoard[i][j] == OCCUPIED_SQUARE && nextSquareLeft == FIXED_SQUARE) {
                    return; // vähemalt üks hõivatud ruut on kohe serva/fikseeritud ruudu kõrval
                            // seega nihutada ei saa
                }
                nextSquareLeft = theBoard[i][j];
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
    private static void moveAPieceRight(int[][] theBoard) {
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
}
