package com.boats.n.models;

/**
 * Created by ben on 8/26/14.
 */
public class World
{
    private int width;
    private int height;
    private Ocean ocean;

    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        ocean = new Ocean(width, height);
    }

    public Ocean getOcean()
    {
        return ocean;
    }
}
