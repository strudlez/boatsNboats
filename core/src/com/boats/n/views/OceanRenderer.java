package com.boats.n.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.FloatGrid;
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
        FloatGrid densities = ocean.getDensity();
        for (int i = 0; i < densities.getWidth() - 1; i++) {
            for (int j = 0; j < densities.getHeight() - 1; j++) {

                float density = (densities.get(i, j + 1)
                    + densities.get(i + 1, j)
                    + densities.get(i + 1, j + 1)) * 0.4f;
                density = MathUtils.clamp(density, 0, 255);

                //batchRenderer.ShapeType.Filled);
                batchRenderer.setColor(0, 0, density / 255.0f, 1);
                //batchRenderer.rect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
                //batchRenderer.end();

                batchRenderer.draw(cellTexture,
                                   i * 10,
                                   j * 10,
                                   10,
                                   10);
            }
        }


        FloatGrid velocityX = ocean.getVelocityX();
        FloatGrid velocityY = ocean.getVelocityY();
        for (int i = 0; i < velocityX.getWidth(); i++) {
            for (int j = 0; j < velocityX.getHeight(); j++) {
                float vX = velocityX.get(i, j);
                float vY = velocityY.get(i, j);

                //float startX = cell.getX() + cell.getSize() / 2.0f;
                Vector2 velocity = new Vector2(vX, vY);
                //float startY = cell.getY() + cell.getSize() / 2.0f;
    
                //float angle = MathUtils.atan2(velocity.y, velocity.x);
    
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
        }

        for (Vector2 position : ocean.getParticles())
        {
            batchRenderer.setColor(new Color(1, 1, 1, 0.1f));
            batchRenderer.draw(dotSprite,
                               position.x,
                               position.y);
        }

        batchRenderer.setColor(Color.WHITE);
    }
}
