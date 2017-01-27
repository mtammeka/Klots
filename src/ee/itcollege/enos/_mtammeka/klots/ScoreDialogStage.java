package ee.itcollege.enos._mtammeka.klots;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Created by Madis on 16.01.2017.
 */
public class ScoreDialogStage extends Stage {
    private ScoreboardKeeper scoreBoard = null;


    ScoreDialogStage(int score) {
        BorderPane root = new BorderPane();
        Text text = new Text("Su skooriks jäi: " + score);


        TextField nameField = new TextField();
        HBox entryWidgets = new HBox();

        VBox fields = new VBox();

        try {
            scoreBoard = new ScoreboardKeeper();
        } catch (IOException e) {
            System.out.println("Edetabeli majandamisega viga kuskil.");
        }
        Text printedBoard = new Text(scoreBoard.getScoreBoard());
        printedBoard.setFont(Font.font ("Courier New", 12));
        // System.out.println(javafx.scene.text.Font.getFamilies());
        Button backButton = new Button("Tagasi");
        fields.getChildren().addAll(text, entryWidgets, printedBoard, backButton);

        root.setCenter(fields);

        Button submitButton = new Button("Sisesta skoor");
        entryWidgets.getChildren().addAll(nameField, submitButton);

        Insets myInset = new Insets(10, 10, 10, 10);
        root.setPadding(myInset);

        backButton.setOnAction(e -> {
            new WelcomeStage();
            try {
                scoreBoard.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            this.close();
        });

        submitButton.setOnAction(e -> {
            String nameEntry = nameField.getText();

            if (scoreBoard.offerNewEntry(nameEntry, score)) {
                printedBoard.setText(scoreBoard.getScoreBoard());
            } else {
                printedBoard.setText("Kahjuks " + nameEntry + " ei mahtunud edetabelisse...\n" + printedBoard.getText());
            }
            try {
                scoreBoard.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            fields.getChildren().removeAll(text, entryWidgets);

        });

        Scene scene = new Scene(root, 400, 300);
        this.setScene(scene);
        this.setTitle("Klots");
        this.show();

        this.setOnCloseRequest(e -> {
            this.close();
            new WelcomeStage();
        });
    }
}
