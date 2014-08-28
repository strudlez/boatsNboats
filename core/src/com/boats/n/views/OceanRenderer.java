package com.boats.n.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Ocean;

import java.util.Random;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Created by ben on 8/26/14.
 */
public class OceanRenderer implements Renderer<Ocean>
{
    @Override
    public void render(ShapeRenderer shapeRenderer, Ocean ocean)
    {
        for (Ocean.Cell<Float> cell : ocean.getDensityCells())
        {
            float density = cell.getValue();

            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(density, density, density, 1);
            shapeRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            shapeRenderer.end();
        }

        for (Ocean.Cell<Vector2> cell : ocean.getVelocityCells())
        {
            Vector2 velocity = cell.getValue();
            float startX = cell.getX() + cell.getSize() / 2.0f;
            float startY = cell.getY() + cell.getSize() / 2.0f;
            float toX = startX + velocity.x * 50;
            float toY = startY + velocity.y * 50;

            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            // shapeRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            shapeRenderer.line(startX, startY, toX, toY);
            shapeRenderer.end();
        }
    }
}
