package com.boats.n.views;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.boats.n.models.Ocean;

/**
 * Created by ben on 8/26/14.
 */
public interface Renderer<T>
{
    public void render(ShapeRenderer shapeRenderer, T model);
}
