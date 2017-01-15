package ee.itcollege.enos._mtammeka.klots;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Madis on 14.01.2017.
 */
public class GameStage extends Stage {
    private final int refreshRate = 6;
    private final int ROWS = 25;
    private final int COLUMNS = 10;
    private long lastTimeStamp = 0;

    GameStage() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("Klots");
        Pane pane = new Pane();
        root.setCenter(pane);
        Button pauseButton = new Button("Paus/Jätka");
        Button resetButton = new Button("Testinupp");
        Text text = new Text("Skoor: ");
        HBox buttonBox = new HBox();
        VBox stuffBox = new VBox();
        buttonBox.getChildren().addAll(pauseButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER);
        stuffBox.getChildren().addAll(buttonBox, text);
        stuffBox.setAlignment(Pos.CENTER);
        root.setBottom(stuffBox);
        root.setBackground(new Background(new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        /*Mängulaua ruutude loomine*/
        GameSquare[][] boardFX = new GameSquare[ROWS][COLUMNS];

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                boardFX[row][column] = new GameSquare(row, column);
                pane.getChildren().add(boardFX[row][column]);
            }
        }
        BoardHandler handler = new BoardHandler(boardFX);

        GameTimer timer = new GameTimer() {
            @Override
            public void handle(long now) {
                // Mängupala liiguks muidu liiga kiiresti
                if ((now - lastTimeStamp > (Math.pow(10, 9)) / refreshRate)) { // now on nanosekundites

                    lastTimeStamp = now;
                    handler.advanceTheGame();

                }
            }
        };

        timer.start();

        pauseButton.setOnAction(e -> {
            if (timer.isRunning()) {
                timer.stop();
            } else {
                timer.start();
            }
        });

        resetButton.setOnAction(e -> {
            System.out.println("eimidagi");
        });

        this.show();
        this.setOnCloseRequest(e -> {
            this.close();
            new WelcomeStage();
        });
    }
}
