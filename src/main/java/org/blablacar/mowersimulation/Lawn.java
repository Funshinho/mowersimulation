package org.blablacar.mowersimulation;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lawn defined by a width and a height.
 */
public class Lawn {

    private int width;
    private int height;
    private AtomicBoolean[][] grid;

    public Lawn(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new AtomicBoolean[width + 1][height + 1];
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                this.grid[i][j] = new AtomicBoolean();
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AtomicBoolean[][] getGrid() {
        return grid;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setGrid(AtomicBoolean[][] grid) {
        this.grid = grid;
    }

}
