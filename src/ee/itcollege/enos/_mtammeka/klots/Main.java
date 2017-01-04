package ee.itcollege.enos._mtammeka.klots;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

public class Main extends Application {

    private TetrisBoard myTetrisBoard;
    private final static int refreshRate = 6;
    private final static int STEP = 20;
    private final static int ROWS = 25;
    private final static int COLUMNS = 10;
    private static long lastTimeStamp = 0;
    private static Set<Byte> commandQueue = new LinkedHashSet<>();
    private boolean pauseStatus = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Klots");

        Pane pane = new Pane();
        root.setCenter(pane);

        Button pauseButton = new Button("PAUSE");
        Button resetButton = new Button("RESET");
        Text text = new Text("Skoor: ");
        HBox buttonBox = new HBox();
        VBox stuffBox = new VBox();
        buttonBox.getChildren().addAll(pauseButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER);
        stuffBox.getChildren().addAll(buttonBox, text);
        stuffBox.setAlignment(Pos.CENTER);

        root.setBottom(stuffBox);
        root.setBackground(new Background(new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        /*Mängulaua nähtavate ruutude loomine*/
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

        // Orienteerumise näited:
        //boardFX[14][0].setFill(Color.BLUE); // rida 13 tulp 1 - siniseks
        //boardFX[ROWS - 1][COLUMNS - 1].setFill(Color.YELLOW); // kõige alumine-parempoolne ruut kollaseks

        /* Luuakse "telgitagune" mängulaud, mängulaua ruutudel on kolm võimalikku seisundit:
         * EMPTY, OCCUPIED, FIXED */
        myTetrisBoard = new TetrisBoard(ROWS, COLUMNS);

        // Anonüümne eksemplar AnimationTimer objektist - teeb võimalikuks, et ekraanil toimub midagi ajas
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Mängupala liiguks muidu liiga kiiresti
                if ((now - lastTimeStamp > (Math.pow(10, 9)) / refreshRate) && !pauseStatus) { // now on nanosekundites

                    lastTimeStamp = now;

                    myTetrisBoard.advanceTheGame(commandQueue);
                    carryBoardToBoardFX(myTetrisBoard.getBoard(), boardFX, ROWS, COLUMNS);
                    text.setText("Skoor: " + myTetrisBoard.getScore());

                    if (myTetrisBoard.isGameOver()) {
                        myTetrisBoard = new TetrisBoard(ROWS, COLUMNS);
                    }
                }
            }
        };

        pauseButton.setOnAction(event -> pauseStatus = !pauseStatus);

        resetButton.setOnAction(event -> myTetrisBoard = new TetrisBoard(ROWS, COLUMNS));

        // Puhver sisendkäskude jaoks tundus alguses hea mõte aga ilmselt mingi hetk tuleb lihtsamaks muuta
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            switch (key.getCode()) {
                case LEFT:
                    commandQueue.add(TetrisBoard.LEFT_ARROW);
                    break;
                case RIGHT:
                    commandQueue.add(TetrisBoard.RIGHT_ARROW);
                    break;
                case DOWN:
                    commandQueue.add(TetrisBoard.DOWN_ARROW);
                    break;
                case UP:
                    commandQueue.add(TetrisBoard.UP_ARROW);
                    break;
                case SPACE:
                    pauseStatus = !pauseStatus;
            }
        });

        timer.start();
        primaryStage.show();

    }

    private static void carryBoardToBoardFX(int[][] inputBoard, Rectangle[][] outputBoardFX, int ROW, int COL) {
        /* meetod mängulaua nähtavaks muutmiseks */
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                switch (inputBoard[i][j]) {
                    case TetrisBoard.OCCUPIED_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.GREEN)
                            outputBoardFX[i][j].setFill(Color.GREEN);
                        break;
                    case TetrisBoard.FIXED_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.VIOLET)
                            outputBoardFX[i][j].setFill(Color.VIOLET);
                        break;
                    case TetrisBoard.EMPTY_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.DARKGRAY)
                            outputBoardFX[i][j].setFill(Color.DARKGRAY);
                        break;
                }
    }
}
