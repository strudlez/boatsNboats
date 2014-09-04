package com.boats.n.controllers;

import java.util.Collection;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Boat;
import com.boats.n.models.Ocean;

public class OceanController {
    private BoatController boatController;

    public OceanController()
    {
        this.boatController = new BoatController();
    }

    public void update(Ocean ocean, Collection<Boat> boats, float dt) {
        ocean.getFluidSolver().step(dt);

        for (Boat boat : boats) {
            pushBoat(boat, ocean, dt);
        }
    }

    public void pushBoat(Boat boat, Ocean ocean, float dt) {
        Vector2 pos = boat.getBody().getPosition();
        boatController.pushBoat(boat, ocean.getVelocityAt(pos).scl(100000));
    }
}
