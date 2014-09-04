package com.boats.n.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public interface Boat {
    public Body getBody();
    public Vector2 getPosition();
    public Vector2 getDisplaySize();
    public Texture getTexture();
}
