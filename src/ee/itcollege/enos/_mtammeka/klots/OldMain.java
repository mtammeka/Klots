package ee.itcollege.enos._mtammeka.klots;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class OldMain extends Application {

    private OldTetrisBoard myOldTetrisBoard;
    private final static int refreshRate = 6;
    private final static int STEP = 20;
    private final static int ROWS = 25;
    private final static int COLUMNS = 10;
    private static long lastTimeStamp = 0;
    private static Set<Byte> commandQueue = new LinkedHashSet<>();
    private boolean pauseStatus = false;
/*

    public static void main(String[] args) {
        launch(args);
    }
*/

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Klots");

        Pane pane = new Pane();
        root.setCenter(pane);

        Button pauseButton = new Button("Paus/Jätka");
        Button resetButton = new Button("Uus mäng");
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

        Alert programStarted = new Alert(Alert.AlertType.CONFIRMATION);
        programStarted.setTitle("Tere!");
        programStarted.setHeaderText("Tetrise kloon \"Klots\"");
        programStarted.setContentText("Mängimiseks kasuta nooleklahve.");
        ButtonType startGame = new ButtonType("Alusta mängu");
        ButtonType exitProgram = new ButtonType("Välju programmist");
        programStarted.getButtonTypes().setAll(startGame, exitProgram);
        Optional<ButtonType> result = programStarted.showAndWait();
        if (result.get() == startGame) {
            /* Luuakse "telgitagune" mängulaud, mängulaua ruutudel on kolm võimalikku seisundit:
             * EMPTY, OCCUPIED, FIXED */
            myOldTetrisBoard = new OldTetrisBoard(ROWS, COLUMNS);
        } else if (result.get() == exitProgram) {
            Platform.exit();
            System.exit(0);
        }


        // Anonüümne eksemplar AnimationTimer objektist - teeb võimalikuks, et ekraanil toimub midagi ajas
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Mängupala liiguks muidu liiga kiiresti
                if ((now - lastTimeStamp > (Math.pow(10, 9)) / refreshRate)) { // now on nanosekundites

                    lastTimeStamp = now;

                    if (!pauseStatus) {
                        myOldTetrisBoard.advanceTheGame(commandQueue);
                        carryBoardToBoardFX(myOldTetrisBoard.getBoard(), boardFX, ROWS, COLUMNS);
                        text.setText("Skoor: " + myOldTetrisBoard.getScore());
                    }

                    if (myOldTetrisBoard.isGameOver()) {
                        text.setText("Mäng läbi! Skoor: " + myOldTetrisBoard.getScore());
                        pauseStatus = true;
                    }

                }
            }
        };

        timer.start();

        pauseButton.setOnAction(event -> pauseStatus = !pauseStatus);

        resetButton.setOnAction(event -> {
            myOldTetrisBoard = new OldTetrisBoard(ROWS, COLUMNS);
            pauseStatus = false;
        });

        // Puhver sisendkäskude jaoks tundus alguses hea mõte aga ilmselt mingi hetk tuleb lihtsamaks muuta
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            switch (key.getCode()) {
                case LEFT:
                    commandQueue.add(OldTetrisBoard.LEFT_ARROW);
                    break;
                case RIGHT:
                    commandQueue.add(OldTetrisBoard.RIGHT_ARROW);
                    break;
                case DOWN:
                    commandQueue.add(OldTetrisBoard.DOWN_ARROW);
                    break;
                case UP:
                    commandQueue.add(OldTetrisBoard.UP_ARROW);
                    break;
                case SPACE:
                    pauseStatus = !pauseStatus;
            }
        });

        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    private static void carryBoardToBoardFX(int[][] inputBoard, Rectangle[][] outputBoardFX, int ROW, int COL) {
        /* meetod mängulaua nähtavaks muutmiseks */
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                switch (inputBoard[i][j]) {
                    case OldTetrisBoard.OCCUPIED_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.GREEN)
                            outputBoardFX[i][j].setFill(Color.GREEN);
                        break;
                    case OldTetrisBoard.FIXED_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.VIOLET)
                            outputBoardFX[i][j].setFill(Color.VIOLET);
                        break;
                    case OldTetrisBoard.EMPTY_SQUARE:
                        if (outputBoardFX[i][j].getFill() != Color.DARKGRAY)
                            outputBoardFX[i][j].setFill(Color.DARKGRAY);
                        break;
                }
    }
}
