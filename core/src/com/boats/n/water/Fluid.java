package com.boats.n.water;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

/**
 * Created by ben on 8/18/14.
 */
public class Fluid
{
    public static final Supplier<Vector2> ZERO_VECTOR_SUPPLIER = new Supplier<Vector2>() {
        @Override
        public Vector2 get() {
            return Vector2.Zero.cpy();
        }
    };

    float timeStep;
    int width;
    int height;

    private Grid<Float> density;
    private Grid<Vector2> velocity;
    private Grid<Float> curl;

    public Fluid(int w, int h, float dt)
    {
        timeStep = dt;
        width = w;
        height = h;

        density = new Grid<Float>(width, height, 0.0f);
        velocity = new Grid<Vector2>(width, height, ZERO_VECTOR_SUPPLIER);
        curl = new Grid<Float>(width, height, 0.0f);
    }

    /**
     * Calculate the curl at position (x, y) in the fluid grid.
     * Physically this represents the vortex strength at the
     * cell. Computed as follows: w = (del x U) where U is the
     * velocity vector at (x, y).
     *
     * @param x The x index of the cell.
     * @param y The y index of the cell.
     **/
    public float getCurl(int x, int y)
    {
        float du_dy = (velocity.get(x, y + 1).y - velocity.get(x, y - 1).y) * 0.5f;
        float dv_dx = (velocity.get(x + 1, y).x - velocity.get(x - 1, y).x) * 0.5f;
        return du_dy - dv_dx;
    }

    public Grid<Vector2> getVorticityConfinement()
    {
        Grid<Vector2> vorticityConfinement = new Grid<Vector2>(width, height, ZERO_VECTOR_SUPPLIER);

        for (int x : curl.getXs())
        {
            for (int y : curl.getYs())
            {
                curl.put(x, y, Math.abs(getCurl(x, y)));
            }
        }

        for (int x = 1; x < width; x++)
        {
            for (int y = 1; y < height; y++)
            {
                float dw_dx = (curl.get(x + 1, y) - curl.get(x - 1, y)) * 0.5f;
                float dw_dy = (curl.get(x, y + 1) - curl.get(x, y - 1)) * 0.5f;

                float length = (float) Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.00001f;

                dw_dx /= length;
                dw_dy /= length;

                float v = getCurl(x, y);

                vorticityConfinement.put(x, y, new Vector2(dw_dy * -v, dw_dx * v));
            }
        }

        return vorticityConfinement;
    }


    private float curl(int x, int y) {
        return 0;
    }
}
