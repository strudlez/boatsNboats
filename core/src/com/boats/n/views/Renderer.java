package com.boats.n.views;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by ben on 8/26/14.
 */
public interface Renderer<T>
{
    public void render(Batch batchRenderer, T model);
}
