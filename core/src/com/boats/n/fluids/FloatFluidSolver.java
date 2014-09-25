package com.boats.n.fluids;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.FloatGrid;

/**
 * Created by ben on 9/2/14.
 */
public interface FloatFluidSolver
{
    void step(float dt);

    void addVelocity(int x, int y, float dx, float dy);

    void addVelocity(int x, int y, Vector2 delta);

    FloatGrid getDensity();

    FloatGrid getVelocityX();
    FloatGrid getVelocityY();

    int getWidth();

    int getHeight();

    Iterable<Vector2> getParticles();
}
