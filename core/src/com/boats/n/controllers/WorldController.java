package com.boats.n.controllers;

import com.boats.n.models.World;

/**
 * Created by ben on 8/26/14.
 */
public class WorldController
{
    public World world;

    public WorldController(World world)
    {
        this.world = world;
    }

    public void update(float dt)
    {
        world.getOcean().update(dt);
    }
}
