package com.boats.n.views;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.boats.n.models.World;

/**
 * Created by ben on 8/26/14.
 */
public class WorldRenderer implements Renderer<World>
{
    private OrthographicCamera cam;

    private OceanRenderer oceanRenderer;

    public WorldRenderer()
    {
        cam = new OrthographicCamera();
        oceanRenderer = new OceanRenderer();
    }

    @Override
    public void render(Batch batchRenderer, World world)
    {
        oceanRenderer.render(batchRenderer, world.getOcean());
    }
}
