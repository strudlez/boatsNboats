package com.boats.n.controllers;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Boat;

public class BoatController {

    public void pushBoat(Boat boat, Vector2 force) {
        boat.getBody().applyForce(force, boat.getBody().getWorldCenter(), true);
    }
}
