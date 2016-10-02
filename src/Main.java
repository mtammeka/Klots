
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.util.Random;

public class Main extends Application{
    private final static int SCENE_WIDTH = 400;
    private final static int SCENE_HEIGHT = 600;
    private final static int STEP = 20; // klotsi laius, samas kui palju vasakule-paremale saab liigutada
    private final static int RECTANGLE_WIDTH = STEP;
    private final static int RECTANGLE_HEIGHT = STEP;
    private final static int ROWS = SCENE_HEIGHT / STEP;
    private final static int COLUMNS = SCENE_WIDTH / STEP;
    private int recX = SCENE_WIDTH / 2; //ristküliku algkoordinaadid on scene keskel
    private int recY = 0;
    long lastTimeStamp = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Pane pane = new Pane();
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);

        Rectangle[][] board = new Rectangle[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                board[i][j] = new Rectangle();
                board[i][j].setX(i * STEP);
                board[i][j].setY(j * STEP);
                board[i][j].setWidth(STEP);
                board[i][j].setHeight(STEP);
                board[i][j].setStroke(Color.GREEN);
                board[i][j].setStrokeWidth(2);
                board[i][j].setFill(Color.WHITE);
                pane.getChildren().add(board[i][j]);
            }
        }
        // board[0][15].setFill(Color.BLUE); // tulp 1 rida 14 siniseks
        // board[COLUMNS - 1][ROWS - 1].setFill(Color.YELLOW); //alumine parempoolseim kollaseks


        // TODO kogu järgnev osa ümber teha, kui ristkülikute maatriksi ideega jätkata tahaks
        Rectangle rec = new Rectangle(recX, recY, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        pane.getChildren().add(rec);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) { //now on nanosekundites
                if (now - lastTimeStamp > (Math.pow(10, 9)) / 7) { //update  x sekundis
                    recY += STEP;
                    rec.setX(recX);
                    rec.setY(recY);

                    if (recY >= SCENE_HEIGHT - STEP) { //jõudsime lõppu?
                        // "kloonime" oma klotsi praeguse asukohaga
                        pane.getChildren().add(new Rectangle(recX, recY, RECTANGLE_WIDTH, RECTANGLE_HEIGHT));
                        // ja "liikuva" klotsi viime algusesse tagasi
                        recY = 0;
                        recX = SCENE_WIDTH / 2;
                    }

                    lastTimeStamp = now;

                }
            }
        };
        timer.start();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            // klotsi liigutamine vasakule-paremale
            switch (key.getCode()) {
                case LEFT:
                    recX -= STEP;
                    break;
                case RIGHT:
                    recX += STEP;
                    break;
                case DOWN:
                    if (recY < SCENE_HEIGHT - (STEP *2)) // muidu võib klotsi "läbi põhja suruda"
                        recY += STEP;
                    break;
            }
        });

        primaryStage.show();
    }
}