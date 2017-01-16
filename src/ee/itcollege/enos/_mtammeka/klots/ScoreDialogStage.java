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
 * Created by Madis on 16.01.2017.
 */
public class ScoreDialogStage extends Stage {
    ScoreDialogStage(int score) {
        BorderPane root = new BorderPane();
        Text text = new Text("Su skooriks jäi: " + score);
        Button backButton = new Button("Tagasi");
        Button addScoreButton = new Button("Lisa skoor");
        root.setCenter(text);
        VBox buttonBox = new VBox();
        buttonBox.getChildren().addAll(backButton/*, addScoreButton*/);
        root.setRight(buttonBox);

        Insets myInset = new Insets(10, 10, 10, 10);
        root.setPadding(myInset);
        buttonBox.setPadding(myInset);
        buttonBox.setAlignment(Pos.CENTER);

        backButton.setOnAction(e -> {
            new WelcomeStage();
            this.close();
        });

        addScoreButton.setOnAction(e -> {
            // mm jep skoori lisamine jne
        });

        Scene scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("Klots");
        this.show();

        this.setOnCloseRequest(e -> {
            this.close();
            new WelcomeStage();
        });
    }
}
