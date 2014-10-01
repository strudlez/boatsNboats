package com.boats.n.containers;

public class FloatGrid {
    private final float[] grid;
    private final int width;
    private final int height;

    public FloatGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new float[width * height];
    }

    public float get(int x, int y) {
        return grid[y * width + x];
    }

    public void set(int x, int y, float f) {
        grid[y * width + x] = f;
    }

    public void add(int x, int y, float f) {
        grid[y * width + x] += f;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
