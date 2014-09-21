package com.boats.n.models;

import java.util.Collection;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.n.boats.TinyBoat;
import com.boats.n.physics.PhysicsWorld;
import com.google.common.collect.Lists;

/**
 * Created by ben on 8/26/14.
 */
public class World
{
    private int width;
    private int height;
    private final Ocean ocean;
    private final PhysicsWorld physicsWorld;
    private final Collection<Boat> boats;

    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        ocean = new Ocean(width, height);
        physicsWorld = new PhysicsWorld();
        boats = Lists.newArrayList();

        boats.add(new TinyBoat(physicsWorld, new Vector2(50, 50)));
    }

    public Ocean getOcean()
    {
        return ocean;
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public Collection<Boat> getBoats() {
        return boats;
    }
}
