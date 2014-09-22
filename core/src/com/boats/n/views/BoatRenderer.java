package com.boats.n.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Boat;

public class BoatRenderer implements Renderer<Boat> {
    public void render(Batch batchRenderer, Boat boat) {
        Vector2 position = boat.getPosition();
        Vector2 size = boat.getDisplaySize();
        batchRenderer.draw(boat.getTexture(),
                position.x,
                position.y,
                size.x,
                size.y);
    }
}
