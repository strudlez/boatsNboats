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
    private OceanController oceanController;

    public WorldController(World world)
    {
        this.world = world;
        this.touchTracker = new TouchTracker(2);
        this.oceanController = new OceanController();
    }

    public void update(float dt)
    {
        oceanController.update(world.getOcean(), world.getBoats(), dt);
        world.getPhysicsWorld().step(dt);
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
        world.getOcean().addVelocity(position, heading.scl(1));
    }
}
