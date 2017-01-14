package ee.itcollege.enos._mtammeka.klots;

import javafx.animation.AnimationTimer;

/**
 * Created by Madis on 14.01.2017.
 */
public abstract class GameTimer extends AnimationTimer {
    GameTimer() {

    }
    // http://stackoverflow.com/questions/40816108/how-can-i-know-if-an-animationtimer-is-running
    // start meetodi override nipp:
    private volatile boolean running; // volatile - muutujat muudetakse võibolla mitmest threadist

    @Override
    public void start() {
        super.start();
        running = true;
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
    }
    public boolean isRunning() {
        return running;
    }
}
