package ee.itcollege.enos._mtammeka.klots;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Madis on 14.01.2017.
 */
public class GameSquare extends Rectangle implements TetrisSquare {

    private static final int STEP = 20;
    private int x, y;

    GameSquare(int row, int column) {
        x = column;
        this.setX(x * STEP);
        y = row;
        this.setY(y * STEP);
        this.setWidth(STEP);
        this.setHeight(STEP);
        this.setStroke(Color.LIGHTGREEN);
        this.setStrokeWidth(2);
        this.setFill(Color.DARKGRAY);
    }
    public int getTetrX() {
        return x;
    }
    public int getTetrY() {
        return y;
    }
    public void setTetrX(int x) { // neid settereid kasutades oleks väga hea klotsid ka massiivis ümber tõsta
        this.x = x;
        this.setX(this.x * STEP);
    }
    public void setTetrY(int y) {
        this.y = y;
        this.setX(this.y * STEP);
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