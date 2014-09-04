package com.boats.n.models.n.boats;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.boats.n.models.Boat;
import com.boats.n.physics.PhysicsWorld;

public class TinyBoat implements Boat {

    private static final float RADIUS = 64;
    private static final float DENSITY = 0.5f;
    private static final float RESTITUTION = 0.5f;
    private static final Vector2 DISPLAY_SIZE = new Vector2(RADIUS * 2, RADIUS * 2);

    private final Body body;
    private final Texture texture;

    public TinyBoat(PhysicsWorld world, Vector2 pos) {
        body = world.createBoat(pos, RADIUS, DENSITY, RESTITUTION);
        texture = new Texture("boat.png");
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public Vector2 getDisplaySize() {
        return DISPLAY_SIZE;
    }
}
