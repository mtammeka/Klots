package ee.itcollege.enos._mtammeka.klots;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
    public final static int UP_L = 6, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4, CUBE = 5, RANDOM = 1; //etc
    public final static int EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2, FIXED_SQUARE = 3;
    private final static int SCENE_WIDTH = 400;
    private final static int SCENE_HEIGHT = 600;
    private final static int STEP = 20;
    private final static int ROWS = SCENE_HEIGHT / STEP;
    private final static int COLUMNS = SCENE_WIDTH / STEP;

    private static boolean fallingPieceExists = false;

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
        Rectangle[][] boardFX = new Rectangle[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                boardFX[i][j] = new Rectangle();
                boardFX[i][j].setX(i * STEP);
                boardFX[i][j].setY(j * STEP);
                boardFX[i][j].setWidth(STEP);
                boardFX[i][j].setHeight(STEP);
                boardFX[i][j].setStroke(Color.LIGHTGREEN);
                boardFX[i][j].setStrokeWidth(2);
                boardFX[i][j].setFill(Color.DARKGRAY);
                pane.getChildren().add(boardFX[i][j]);
            }
        }
        System.out.printf("COLUMNS: %d, ROWS: %d.\n", COLUMNS, ROWS);
        // boardFX[0][15].setFill(Color.BLUE); // tulp 1 rida 14 siniseks
        // boardFX[COLUMNS - 1][ROWS - 1].setFill(Color.YELLOW); //alumine parempoolseim kollaseks

        /*Actual game board - only info held by squares is their state -
         * EMPTY, OCCUPIED or FIXED. The location of a square is known through [i][j] coordinates */
        int[][] board = new int[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                board[i][j] = EMPTY_SQUARE;
            }
        }

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (!fallingPieceExists) {
                    spawnAPiece(board, COLUMNS / 2, 0, RANDOM);
                }
                /* stuff happens */

                carryBoardToBoardFX(board, boardFX, COLUMNS, ROWS);
            }
        };

        timer.start();

        primaryStage.show();

    }

    private static void carryBoardToBoardFX(int[][] inputBoard, Rectangle[][] outputBoardFX, int COL, int ROW) {
        for (int i = 0; i < COL; i++)
            for (int j = 0; j < ROW; j++)
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

    static void spawnAPiece(int[][] targetBoard, int COL_OFFSET, int ROW_OFFSET, int PIECETYPE) {
        //TODO this function puts a complete piece in the target board
        //UP_L = 6, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4, CUBE = 5, RANDOM = 1; //etc
        //I guess putting COLUMNS in the inner array wasn't so smart as now all these are
        //"mirrored and left-rotated 90 degrees lulz//
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
        /* siin võiks liikuda target board x ja y koordinaatidele, teha seal nt 4x4 kasti mittefikseeritud ruute
        täiesti puhtaks JAA tekitada uue klotsi */
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (targetBoard[i + COL_OFFSET][j + ROW_OFFSET] != FIXED_SQUARE) {
                    targetBoard[i + COL_OFFSET][j + ROW_OFFSET] = EMPTY_SQUARE;
                }
            }
        }

        for (int i = 0; i < upL.length; i++) {
            for (int j = 0; j < upL[i].length; j++) {
                targetBoard[i + COL_OFFSET][j + ROW_OFFSET] = upL[i][j];
            }
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