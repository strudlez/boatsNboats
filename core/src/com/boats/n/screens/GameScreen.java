package com.boats.n.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.boats.n.controllers.WorldController;
import com.boats.n.models.World;
import com.boats.n.views.WorldRenderer;

/**
 * Created by ben on 8/26/14.
 */
public class GameScreen implements Screen
{
    private World world;
    private WorldRenderer worldRenderer;
    private WorldController worldController;
    private Batch batchRenderer;

    public GameScreen()
    {
        world = new World(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldRenderer = new WorldRenderer();
        worldController = new WorldController(world);
        batchRenderer = new SpriteBatch();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldController.update(delta);

        batchRenderer.begin();
        worldRenderer.render(batchRenderer, world);
        batchRenderer.end();
    }

    @Override
    public void resize(int width, int height)
    {
    }

    @Override
    public void show()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}
