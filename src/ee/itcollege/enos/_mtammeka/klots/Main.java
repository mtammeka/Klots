package ee.itcollege.enos._mtammeka.klots;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Madis on 14.01.2017.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        new WelcomeStage();
    }
}
