package ee.itcollege.enos._mtammeka.klots;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Madis on 14.01.2017.
 */
public class GameSquare extends Rectangle implements TetrisSquare {

    private static final int STEP = 20;

    GameSquare(int row, int column) {
        this.setX(column * STEP);
        this.setY(row * STEP);
        this.setWidth(STEP);
        this.setHeight(STEP);
        this.setStroke(Color.LIGHTGREEN);
        this.setStrokeWidth(2);
        this.setFill(Color.DARKGRAY);
    }

    public void setOccupied() {
        this.setFill(Color.GREEN);
    }

    public boolean isOccupied() {
        return this.getFill().equals(Color.GREEN);
    }

    public void setFixed() {
        this.setFill(Color.VIOLET);
    }

    public boolean isFixed() {
        return this.getFill().equals(Color.VIOLET);
    }

    public void setEmpty() {
        this.setFill(Color.DARKGRAY);
    }

    public boolean isEmpty() {
        return this.getFill().equals(Color.DARKGRAY);
    }


}