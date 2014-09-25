package com.boats.n.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.controllers.WorldController;
import com.boats.n.models.World;
import com.boats.n.views.WorldRenderer;

/**
 * Created by ben on 8/26/14.
 */
public class GameScreen implements Screen, InputProcessor
{

    private static int WORLD_WIDTH = 640;
    private static int WORLD_HEIGHT = 480;

    private static float VIRTUAL_WIDTH = 640;
    private static float VIRTUAL_HEIGHT = 480;

    private World world;
    private WorldRenderer worldRenderer;
    private WorldController worldController;
    private Batch batchRenderer;
    private OrthographicCamera cam;

    public GameScreen()
    {
        world = new World(WORLD_WIDTH, WORLD_HEIGHT);
        worldRenderer = new WorldRenderer();
        worldController = new WorldController(world);
        batchRenderer = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        cam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldController.update(delta);

        cam.update();
        batchRenderer.setProjectionMatrix(cam.combined);

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

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        worldController.pointerDown(pointer, new Vector2(screenX, screenY).scl(getScale()));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        worldController.pointerUp(pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        worldController.pointerMove(pointer, new Vector2(screenX, screenY).scl(getScale()));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

    private Vector2 getScale()
    {
        return new Vector2(cam.viewportWidth / Gdx.graphics.getWidth(),
                cam.viewportWidth / Gdx.graphics.getHeight());
    }
}
