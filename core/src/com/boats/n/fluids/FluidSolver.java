package com.boats.n.fluids;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.Grid;

/**
 * Created by ben on 9/2/14.
 */
public interface FluidSolver
{
    void step(float dt);

    void addVelocity(int x, int y, float dx, float dy);

    void addVelocity(int x, int y, Vector2 delta);

    Grid<Float> getDensity();

    Grid<Vector2> getVelocity();

    int getWidth();

    int getHeight();
}
