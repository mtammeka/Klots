package ee.itcollege.enos._mtammeka.klots;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Madis on 14.01.2017.
 */
public class GameStage extends Stage {

    GameStage() {
        final int refreshRate = 6;
        final int STEP = 20;
        final int ROWS = 25;
        final int COLUMNS = 10;
        long lastTimeStamp = 0;
        Set<Byte> commandQueue = new LinkedHashSet<>();
        boolean pauseStatus = false;

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("Klots");

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

        this.show();

    }

}
