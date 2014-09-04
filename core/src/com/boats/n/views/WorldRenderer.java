package com.boats.n.views;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Boat;
import com.boats.n.models.World;

/**
 * Created by ben on 8/26/14.
 */
public class WorldRenderer implements Renderer<World>
{
    private OrthographicCamera cam;

    private OceanRenderer oceanRenderer;
    private BoatRenderer boatRenderer;

    public WorldRenderer()
    {
        cam = new OrthographicCamera();
        oceanRenderer = new OceanRenderer();
        boatRenderer = new BoatRenderer();
    }

    @Override
    public void render(Batch batchRenderer, World world)
    {
        oceanRenderer.render(batchRenderer, world.getOcean());

        for (Boat boat : world.getBoats()) {
            boatRenderer.render(batchRenderer, boat);
        }
    }
}
