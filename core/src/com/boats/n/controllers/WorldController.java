package com.boats.n.controllers;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.World;
import com.boats.n.objects.TouchTracker;

/**
 * Created by ben on 8/26/14.
 */
public class WorldController
{
    public World world;
    private TouchTracker touchTracker;

    public WorldController(World world)
    {
        this.world = world;
        this.touchTracker = new TouchTracker(2);
    }

    public void update(float dt)
    {
        world.getOcean().update(dt);
    }

    public void pointerUp(Integer id)
    {
        touchTracker.pointerUp(id);
    }

    public void pointerDown(Integer id, Vector2 position)
    {
        touchTracker.pointerDown(id, position);
    }

    public void pointerMove(Integer id, Vector2 position)
    {
        Vector2 heading = touchTracker.pointerMove(id, position);
        world.getOcean().addVelocity(position, heading);
    }
}
