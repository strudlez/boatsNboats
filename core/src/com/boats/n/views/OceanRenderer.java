package com.boats.n.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.models.Ocean;

/**
 * Created by ben on 8/26/14.
 */
public class OceanRenderer implements Renderer<Ocean>
{
    private final Texture cellTexture;
    private final Sprite arrowSprite;
    private final Sprite dotSprite;

    public OceanRenderer()
    {
        cellTexture = new Texture("badlogic.jpg");
        arrowSprite = new Sprite(new Texture("arrow.png"));
        dotSprite = new Sprite(new Texture("dot.png"));
    }

    @Override
    public void render(Batch batchRenderer, Ocean ocean)
    {
        for (Ocean.Cell<Float> cell : ocean.getDensityCells())
        {
            float density = MathUtils.clamp(cell.getValue(), 0, 255);
            //batchRenderer.ShapeType.Filled);
            batchRenderer.setColor(0, 0, density / 255.0f, 1);
            //batchRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            //batchRenderer.end();

            batchRenderer.draw(cellTexture,
                               cell.getX(),
                               cell.getY(),
                               cell.getSize(),
                               cell.getSize());
        }

        for (Ocean.Cell<Vector2> cell : ocean.getVelocityCells())
        {
            Vector2 velocity = cell.getValue();
            float startX = cell.getX() + cell.getSize() / 2.0f;
            float startY = cell.getY() + cell.getSize() / 2.0f;

            float angle = MathUtils.atan2(velocity.y, velocity.x);

            /* arrowSprite.setX(startX);
            arrowSprite.setY(startY);
            arrowSprite.setScale(velocity.len() * 0.5f, 0.03f);
            arrowSprite.setRotation(MathUtils.radiansToDegrees * angle); */
            // arrowSprite.draw(batchRenderer);

            //batchRenderer.ShapeType.Filled);
            /* batchRenderer.setColor(Math.abs(velocity.x), velocity.len2(), Math.abs(velocity.y), 0.5f);
            //batchRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            //batchRenderer.end();

            batchRenderer.draw(cellTexture,
                               cell.getX(),
                               cell.getY(),
                               cell.getSize(),
                               cell.getSize());

            /*
            shapeRenderer.setProjectionMatrix(batchRenderer.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            // shapeRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
            shapeRenderer.line(startX, startY, toX, toY);
            shapeRenderer.end();
            */
        }

        /*
        for (Vector2 position : ocean.getParticles())
        {
            batchRenderer.setColor(new Color(1, 1, 1, 0.1f));
            batchRenderer.draw(dotSprite,
                               position.x,
                               position.y);
        }
        */
    }
}
