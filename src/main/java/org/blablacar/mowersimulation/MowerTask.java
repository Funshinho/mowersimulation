package org.blablacar.mowersimulation;

import static org.blablacar.mowersimulation.Orientation.E;
import static org.blablacar.mowersimulation.Orientation.N;
import static org.blablacar.mowersimulation.Orientation.S;
import static org.blablacar.mowersimulation.Orientation.W;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

/**
 * Task to execute the steps of a mower
 */
public class MowerTask implements Callable<String> {

    private static final Logger log = Logger.getLogger(MowerTask.class);

    private String id;
    private Lawn lawn;
    private Mower mower;

    public MowerTask(String id, Lawn lawn, Mower mower) {
        this.id = id;
        this.lawn = lawn;
        this.mower = mower;
    }

    @Override
    public String call() {
        log.info("Start moving " + id);
        List<Direction> steps = mower.getSteps();
        for (Direction step : steps) {
            if (Direction.F.equals(step)) {
                computePosition(lawn, mower);
            } else {
                computeOrientation(step, mower);
            }
            log.debug("Position " + id + " : " + mower.getX() + " " + mower.getY() + " " + mower.getOrientation());
        }
        return String.join(" ", Arrays.asList(id + " :", String.valueOf(mower.getX()), String.valueOf(mower.getY()),
                mower.getOrientation().name()));
    }

    /**
     * Computes next position of the mower
     *
     * @param lawn  the lawn
     * @param mower the mower
     */
    public void computePosition(Lawn lawn, Mower mower) {
        int x = mower.getX();
        int y = mower.getY();
        int nextX = x;
        int nextY = y;
        switch (mower.getOrientation()) {
            case N:
                nextY = Math.min(nextY + 1, lawn.getHeight());
                break;
            case E:
                nextX = Math.min(nextX + 1, lawn.getWidth());
                break;
            case W:
                nextX = Math.max(nextX - 1, 0);
                break;
            case S:
                nextY = Math.max(nextY - 1, 0);
                break;
            default:
                break;
        }
        // Atomically move the mower to the next position
        if (lawn.getGrid()[nextX][nextY].compareAndSet(false, true)) {
            mower.setX(nextX);
            mower.setY(nextY);
            lawn.getGrid()[x][y].set(false);
        } else {
            log.debug("Cannot move " + id + ": Position " + nextX + " " + nextY + " already occupied");
        }
    }

    /**
     * Computes orientation of the mower
     *
     * @param step  the next direction
     * @param mower the mower
     */
    public void computeOrientation(Direction step, Mower mower) {
        switch (mower.getOrientation()) {
            case N:
                mower.setOrientation(Direction.L.equals(step) ? W : E);
                break;
            case E:
                mower.setOrientation(Direction.L.equals(step) ? N : S);
                break;
            case W:
                mower.setOrientation(Direction.L.equals(step) ? S : N);
                break;
            case S:
                mower.setOrientation(Direction.L.equals(step) ? E : W);
                break;
            default:
                break;
        }
    }
}
