package ee.itcollege.enos._mtammeka.klots;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main extends Application {
    private final static int refreshRate = 6;
    private final static int UP_L = 1, DOWN_L = 2, RIGHT_L = 3, LEFT_L = 4,
                            UP_TRANS_L = 5, DOWN_TRANS_L = 6, LEFT_TRANS_L = 7, RIGHT_TRANS_L = 8,
                            CUBE = 9,
                            UP_T = 10, LEFT_T = 11, RIGHT_T = 12, DOWN_T = 13,
                            UP_DOWN_S = 14, LEFT_RIGHT_S = 15,
                            UP_DOWN_TRANS_S = 16, LEFT_RIGHT_TRANS_S = 17,
                            DOWN_BAR = 18, UP_BAR = 19,
                            RANDOM = 20;
    private static int globalCurrentPieceType = 0;
    private final static int EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2,
                            FIXED_SQUARE = 3, COMPLETED_SQUARE = 4;
    private final static int STEP = 20;
    private final static int ROWS = 20;
    private final static int COLUMNS = 10;
    private final static int SCENE_WIDTH = COLUMNS * STEP;
    private final static int SCENE_HEIGHT = (ROWS * STEP) + 100;
    private static int score = 0;
    private static long lastTimeStamp = 0;
    private static boolean fallingPieceExists = false;
    private static boolean gameOver = false;
    private static Set<Byte> commandQueue = new LinkedHashSet<>();
    private final static byte DOWN_ARROW = 1, LEFT_ARROW = 2, RIGHT_ARROW = 3, UP_ARROW = 4;
    private boolean pauseStatus = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Pane pane = new Pane();
        /*Scene constructor'i argumendid - parent root, x, y, fill color*/
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);

        // Tekstiala mängu nimega, skooriga jne
        // kõik selle võiks ilust VBox jne asjadega teha tegelikult

        Button pauseButton = new Button("PAUSE");
        Group group = new Group();
        Text text = new Text("TETRIS\nTETRIS");

        group.getChildren().addAll(text);
        pane.getChildren().addAll(group, pauseButton);
        pauseButton.setLayoutX((SCENE_WIDTH / 2) - 25); // getWidth'iga ei saa nuppu keskele miskipärast?
        pauseButton.setLayoutY(SCENE_HEIGHT - 90 );
        group.setLayoutX((SCENE_WIDTH - text.getLayoutBounds().getWidth()) / 2);
        group.setLayoutY(SCENE_HEIGHT - 10);

        text.setText("TETRIS\nTETRIS");

        pane.setBackground(new Background(new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, Insets.EMPTY)));

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
                if ((now - lastTimeStamp > (Math.pow(10, 9)) / refreshRate) && !pauseStatus) { // now on nanosekundites

                    lastTimeStamp = now;

                    if (!fallingPieceExists) {
                        // mäng algas või klots just fikseeriti
                        // vaja kokku lugeda täiesti fikseeritud read ja need "vahelt ära tõmmata"
                        // anda punktid
                        score += removeCompletedLines(board);
                        text.setText("Skoor: " + score);
                        group.setLayoutX((SCENE_WIDTH - text.getLayoutBounds().getWidth()) / 2);

                        // mäng läbi, kui uue klotsi tekitamisele jääb midagi ette
                        gameOver = !spawnAPiece(board, 0, COLUMNS / 2, RANDOM);
                    } else {
                        advanceAPiece(board);
                        try {
                            applyUserInput(board);
                        } catch (ConcurrentModificationException e) {
                            // ära tee midagi haha
                            // ("produktsiooniversioonis" ikkagi ilmselt teeme)
                        }
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

        pauseButton.setOnAction(event -> pauseStatus = !pauseStatus);

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
                case UP:
                    commandQueue.add(UP_ARROW);
                    break;
                case SPACE:
                    pauseStatus = !pauseStatus;
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

    private static boolean spawnAPiece(int[][] targetBoard, final int ROW_OFFSET, final int COL_OFFSET, int pieceType) {
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
            pieceType = (int) (Math.random() * 19) + 1; //nr 20 ongi "juhuslik", 1..19 on konkreetsed palad
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
       int[][] tempBoard = new int[ROWS][COLUMNS];
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

    private static void moveAPieceLeft(int[][] theBoard) {
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

    private static void tryRotateCurrentPiece(int[][] theBoard) {
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
        int[][] temporaryTheBoard = new int[ROWS][COLUMNS];
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

        return;
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

    private static int removeCompletedLines(int[][] theBoard) {

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
