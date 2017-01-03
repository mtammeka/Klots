package ee.itcollege.enos._mtammeka.klots;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

public class Main extends Application {

    private TetrisBoard myTetrisBoard;
    private final static int refreshRate = 6;
    private final static int EMPTY_SQUARE = 1, OCCUPIED_SQUARE = 2,
                            FIXED_SQUARE = 3;
    private final static int STEP = 20;
    private final static int ROWS = 20;
    private final static int COLUMNS = 10;
    private final static int SCENE_WIDTH = COLUMNS * STEP;
    private final static int SCENE_HEIGHT = (ROWS * STEP) + 100;
    private static long lastTimeStamp = 0;
    private static Set<Byte> commandQueue = new LinkedHashSet<>();
    private final static byte DOWN_ARROW = 1, LEFT_ARROW = 2, RIGHT_ARROW = 3, UP_ARROW = 4;
    private boolean pauseStatus = false;

    int board[][] = new int[ROWS][COLUMNS];

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

        myTetrisBoard = new TetrisBoard(ROWS, COLUMNS);

        board = myTetrisBoard.getBoard();


        // Anonüümne eksemplar AnimationTimer objektist - teeb võimalikuks, et ekraanil toimub midagi ajas
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Mängupala liiguks muidu liiga kiiresti
                if ((now - lastTimeStamp > (Math.pow(10, 9)) / refreshRate) && !pauseStatus) { // now on nanosekundites

                    lastTimeStamp = now;

                    myTetrisBoard.advanceTheGame(commandQueue);
                    board = myTetrisBoard.getBoard();
                    carryBoardToBoardFX(board, boardFX, ROWS, COLUMNS);
                    text.setText("Skoor: " + myTetrisBoard.getScore());

                    if (myTetrisBoard.isGameOver()) {
                        myTetrisBoard = new TetrisBoard(ROWS, COLUMNS);
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
}
