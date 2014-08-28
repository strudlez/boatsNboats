package com.boats.n.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Ocean;

/**
 * Created by ben on 8/26/14.
 */
public class OceanRenderer implements Renderer<Ocean>
{
    //Texture texture;

    public OceanRenderer()
    {
        //texture = new  Texture("badlogic.jpg");
    }
    @Override
    public void render(Batch batchRenderer, Ocean ocean)
    {
        for (Ocean.Cell<Float> cell : ocean.getDensityCells())
        {
            float density = cell.getValue();

            //batchRenderer.ShapeType.Filled);
            batchRenderer.setColor(density, density, density, 1);
            //batchRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            //batchRenderer.end();
            
            /*batchRenderer.draw(texture,
                    cell.getX(),
                    cell.getY(),
                    cell.getSize(),
                    cell.getSize());*/
        }

        for (Ocean.Cell<Vector2> cell : ocean.getVelocityCells())
        {
            Vector2 velocity = cell.getValue();
            float startX = cell.getX() + cell.getSize() / 2.0f;
            float startY = cell.getY() + cell.getSize() / 2.0f;
            float toX = startX + velocity.x * 50;
            float toY = startY + velocity.y * 50;

            //batchRenderer.begin(ShapeType.Line);
            batchRenderer.setColor(Color.RED);
            // shapeRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            //batchRenderer.line(startX, startY, toX, toY);
            //batchRenderer.end();
        }
    }
}
