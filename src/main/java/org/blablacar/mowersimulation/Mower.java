package org.blablacar.mowersimulation;

import java.util.List;

/**
 * Mower defined by its position, orientation and a lst of steps. 
 */
public class Mower {

    private int x;
    private int y;
    private Orientation orientation;
    private List<Direction> steps;

    public Mower(int x, int y, Orientation orientation, List<Direction> steps) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.steps = steps;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public List<Direction> getSteps() {
        return steps;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setSteps(List<Direction> steps) {
        this.steps = steps;
    }

}

