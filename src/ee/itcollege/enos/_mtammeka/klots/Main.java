package ee.itcollege.enos._mtammeka.klots;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    // TODO muutujate scope üle vaadata, palju ebavajalikku on siin ilmselt
    public final static int UP_L = 6, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4, CUBE = 5, RANDOM = 1; //etc
    public final static int EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2, FIXED_SQUARE = 3;
    private final static int SCENE_WIDTH = 400;
    private final static int SCENE_HEIGHT = 600;
    private final static int STEP = 20;
    private final static int ROWS = SCENE_HEIGHT / STEP;
    private final static int COLUMNS = SCENE_WIDTH / STEP;
    private long lastTimeStamp = 0;
    private static boolean fallingPieceExists = false;
    private static boolean gameOver = false; // kui tüki tekitamiseks pole ruumi... spawnAPiece ise kontrollib?
    static ArrayList<Byte> commandQueue = new ArrayList<>();
    public final static byte DOWN_ARROW = 1, LEFT_ARROW = 2, RIGHT_ARROW = 3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        /*arguments for Scene constructor - parent root, x, y, fill color*/
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT, Color.CYAN);
        primaryStage.setScene(scene);

        /*Initial drawing of "game board"*/
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
        //boardFX[14][0].setFill(Color.BLUE); // row 13 column 1 - paint blue
        //boardFX[ROWS - 1][COLUMNS - 1].setFill(Color.YELLOW); // lowest rightmost square - paint yellow

        /*Actual game board - only info held by squares is their state -
         * EMPTY, OCCUPIED or FIXED. The location of a square is known through [i][j] coordinates */
        int[][] board = new int[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = EMPTY_SQUARE;
            }
        }

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now - lastTimeStamp > (Math.pow(10, 9)) / 10) { //update ?xsekundis natiivne refresh on vist 60fps
                    lastTimeStamp = now;

                    if (!fallingPieceExists) {
                        spawnAPiece(board, 0, COLUMNS / 2, RANDOM);
                    } else {
                        applyUserInput(board);
                        advanceAPiece(board);

                    }
                    /* stuff happens */

                    carryBoardToBoardFX(board, boardFX, ROWS, COLUMNS);

                    if (gameOver == true) {
                        System.out.println("Game over");
                        Platform.exit();
                        System.exit(0);
                    }

                }
            }
        };

        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            // klotsi liigutamine vasakule-paremale

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

    static void spawnAPiece(int[][] targetBoard, final int ROW_OFFSET, final int COL_OFFSET, int pieceType) {
        //TODO this function puts a complete piece in the target board
        //expected to be called only if no pieces exist ... I guess

        int[][] upL =       { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XO
                            {OCCUPIED_SQUARE, EMPTY_SQUARE},                        // XO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // XX

        int[][] downL =     { {OCCUPIED_SQUARE, EMPTY_SQUARE},                      // XX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE},                        // OX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // OX

        int[][] leftL =     { {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE},  // XXX
                            {OCCUPIED_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE} };        // XOO

        int[][] rightL =    { {OCCUPIED_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE},        // XOO
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE, OCCUPIED_SQUARE} };  // XXX

        int[][] cube =      { {OCCUPIED_SQUARE, OCCUPIED_SQUARE},                   // XX
                            {OCCUPIED_SQUARE, OCCUPIED_SQUARE} };                   // XX

        int[][] currentPiece = cube; // have to initialize to something..?

        // clear from previous pieces in case rotating etc... maybe handle cleaning elswhere though?
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (targetBoard[i + ROW_OFFSET][j + COL_OFFSET] != FIXED_SQUARE) {
                    targetBoard[i + ROW_OFFSET][j + COL_OFFSET] = EMPTY_SQUARE;
                } else {
                    gameOver = true;
                }
            }
        }

        //UP_L = 6, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4, CUBE = 5, RANDOM = 1; //etc
        if (pieceType == RANDOM)
            pieceType = (int) (Math.random() * 5) + 2; //1 is random, 2...6 are pieces

        switch (pieceType) {
            case UP_L:      currentPiece = upL;
                            break;
            case DOWN_L:    currentPiece = downL;
                            break;
            case RIGHT_L:   currentPiece = rightL;
                            break;
            case LEFT_L:    currentPiece = leftL;
                            break;
            case CUBE:      currentPiece = cube;
                            break;
        }

        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[i].length; j++) {
                targetBoard[i + ROW_OFFSET][j + COL_OFFSET] = currentPiece[i][j];
            }
        }
        // got this far so we definitely have a piece on the board now right?
        fallingPieceExists = true;
    }

    static void advanceAPiece(int[][] theBoard) {
        /*ascend top->down column by column, advance what's found*/
        /*theBoard[row][column]*/
        int advancables;
        for (int j = 0; j < theBoard[0].length; j++) { // mitu tulpa
            advancables = 0;

            for (int i = 0; i < theBoard.length; i++) { // mitu rida, st elementi tulbas
                if (theBoard[i][j] == OCCUPIED_SQUARE) {
                    advancables++;
                    /*
                    *
                    * JÄTKA SIIT HAHA
                    *
                    * */
                    // siin if-is ma väga usun et vasakult paremale hinnatakse '||' statementi ja kui
                    // i+1==theboard.length on TÕSI EHK JÄRGMIST ELEMENTI EI OLE
                    // siis ei hakata uurima KAS JÄRGMINE ELEMENT VÕIKS OLLA FIKSEERITUD RUUT
                    if (((i + 1) == theBoard.length) || (theBoard[i + 1][j] == FIXED_SQUARE))   { // oleme lõpus all ? või millegi vastas?

                        for (int k = 0; k < theBoard[0].length; k++) {
                            for (int l = 0; l < theBoard.length ; l++) {
                                if (theBoard[l][k] == OCCUPIED_SQUARE)
                                    theBoard[l][k] = FIXED_SQUARE;

                            }

                        }

                        //


                        fallingPieceExists = false;
                    }


                } else if (theBoard[i][j] == EMPTY_SQUARE && advancables != 0) {
                    theBoard[i - advancables][j] = EMPTY_SQUARE;
                    theBoard[i][j] = OCCUPIED_SQUARE;
                    advancables = 0;
                }
            }

        }
    }

    static void applyUserInput(int[][] theBoard) {
        if (!commandQueue.isEmpty()) {
            switch (commandQueue.remove(0)) {
                case LEFT_ARROW:
                    System.out.println("official moving action");
                    moveAPieceLeft(theBoard);
                    break;
            }
        }

    }
    static void moveAPieceLeft(int[][] theBoard) {
        // we start moving on last square on row 0 - theboard[0][theBoard[0].length]
        // we move to the left "<-----"   ;  add up advancables and remove the rightmost and add the leftmost
        int advancables = 0;
        for (int i = 0; i < theBoard.length; i++) { // start on row 0, move down
            for (int j = theBoard[i].length - 1; j >= 0; j--) { // start on last column
                if (theBoard[i][j] == OCCUPIED_SQUARE) {
                    advancables++;
                } else if (theBoard[i][j] == EMPTY_SQUARE && advancables != 0) {
                    theBoard[i][j] = OCCUPIED_SQUARE;
                    theBoard[i][j + advancables] = EMPTY_SQUARE;
                    advancables = 0;
                }
            }

            System.out.println("one run");
        }
    }
}


        /* animationtimer sees toimub tegevus:

             - on olemas staatusmuutuja - kas vabalangev klots on olemas v ei? kui ei, loositakse uus.
             - kui vabalangevat klotsi ei ole, nö spawnitakse klots - maatriksis märgitakse ära keskel
             üleval klotsi kujule vastav ala. JA staatusmuutuja=on_olemas_Vabalangev_klots

             - ps maatriks pole boolean - on kolm olekut: ruut on vaba, ruutu täidab hetkel nö langeva
             klotsi osa; või kolmas variant - ruutu täidab _fikseeritud_ klotsi osa

             - igal juhul skännib iga tsükli jooksul maatriksit läbi "klotsiliigutamise jõud"
                - käib ülevalt alla maatriksi läbi, kui leiab nö vabalangeva klotsi
                        - siis kontrollib kas alumine ruut on vaba....
                            - seda peab kontrollima KÕIGI vabalangevate klotside kohta
                                - kui olid KÕIK alumised vabad siis liigutab kõiki langevaid ruute allapoole
                        - kui kasvõi üks ei olnud alt vaba, käib kõik klotsi elemendid läbi fikseerides need
                                JA muudab staatusmuutuja, et spawnitakse uus langev klots

             - SIIN ERALDI LATERAALSE LIIKUMISE JÕUD KA
             - mingi teine skänner otsib täiesti täis rida (fikseeritud ruute), mis eemaldatakse ja antakse punkte
                - pärast eemaldamist liigutatakse _kogu_ülemist osa allapoole, SH "FIKSEERITUD" RUUDUD

             - kuskil on keyeventlistener, mis EI TOIMI OTSE, vaid saadab kasutaja käigud kuskile puhvrisse
             - puhvrit loetakse ka "klotsiliigutamise" juures - 2x samm kui oli nooleklahv alla
             - kui kasutaja on tahtnud lateraalset liigutamisest, on eraldi samm lateraalse liigutamise kohta
             (Vt eespool)

             - lõpuks joonistatakse hetkeseis ekraanile

         */