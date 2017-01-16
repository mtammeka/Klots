package ee.itcollege.enos._mtammeka.klots;

import javafx.animation.AnimationTimer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Madis on 14.01.2017.
 */
public class BoardHandler {
    private int score = 0;
    Text text;
    private AnimationTimer timer;
    private Stage parentStage;
    private int rows, columns;
    private GameSquare[][] board;
    private boolean fallingPieceExists = false, isGameOver = false;
    private PieceSquare[][] currentPiece;
    private volatile int leftShiftCount = 0, rightShiftCount = 0, rotateCount = 0, speedUpCount = 0;
    private ArrayList<Piece> pieces;

    BoardHandler(GameSquare[][] board) {
        this.board = board;
        rows = board.length;
        columns = board[0].length; // eeldame ühepikkuseid ridu!!

        try {
            pieces = new PieceImporter().getPieces();
        } catch (IOException e) {
            System.out.println("pieces.txt failis ilmselt midagi valesti");
            System.out.println(e);
        }
    }

    public void advanceTheGame() {
        if (!fallingPieceExists) {
            int tempScore = ScoreCounter.removeLines(board);
            if (tempScore > 0) {
                score += tempScore;
                text.setText("Skoor: " + score);
            }

            currentPiece = pieces.get((int)(pieces.size() * Math.random())).getPiece();

            isGameOver = !PieceSpawner.spawn(board, currentPiece);
            fallingPieceExists = true;
        } else {
            // üritame edasi liigutada

            fallingPieceExists = PieceAdvancer.advance(board); // true kui ei fikseerinud klotsi ära
        }
        if (isGameOver) {
            parentStage.close();
            timer.stop();
            new ScoreDialogStage(score);
        }
    }

    void applyInput() {
        if (fallingPieceExists) {
            if (leftShiftCount > 0) {
                PieceShifter.shiftLeft(board);
                leftShiftCount = 0;
            }
            if (rightShiftCount > 0) {
                PieceShifter.shiftRight(board);
                rightShiftCount = 0;
            }
            if (speedUpCount > 0) {
                this.advanceTheGame();
                speedUpCount = 0;
            }
            if (rotateCount > 0) {
                PieceSquare[][] rotatedPiece = Piece.getNextRotation(currentPiece);
                if (Rotator.rotate(board, rotatedPiece)) {
                    currentPiece = rotatedPiece;
                }
                rotateCount = 0;
            }
        }

    }

    void shiftLeft() {
        leftShiftCount++;
    }
    void shiftRight() {
        rightShiftCount++;
    }

    void rotate() {
        rotateCount++;
    }

    void speedUp() {
        speedUpCount++;
    }

    void setStage(Stage stage) {
        parentStage = stage;
    }
    void setTimer(AnimationTimer timer) {
        this.timer = timer;
    }
    void setScoreBoard(Text text) {
        this.text = text;
    }
}
