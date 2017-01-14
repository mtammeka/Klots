package ee.itcollege.enos._mtammeka.klots;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by Madis on 14.01.2017.
 */
public class WelcomeStage extends Stage {

    WelcomeStage() {
        BorderPane welcomeRoot = new BorderPane();
        Text welcomeText = new Text("Mängimiseks kasuta nooleklahve.");
        Button startGame = new Button("Alusta mängu");
        Button exitProgram = new Button("Välju programmist");
        welcomeRoot.setCenter(welcomeText);
        VBox buttonBox = new VBox();
        buttonBox.getChildren().addAll(startGame, exitProgram);
        welcomeRoot.setRight(buttonBox);

        Insets myInset = new Insets(10, 10, 10, 10);
        welcomeRoot.setPadding(myInset);
        buttonBox.setPadding(myInset);
        buttonBox.setAlignment(Pos.CENTER);

        startGame.setOnAction(e -> {
            new GameStage();
        });

        exitProgram.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        Scene scene = new Scene(welcomeRoot);
        this.setScene(scene);
        this.show();
    }
}
