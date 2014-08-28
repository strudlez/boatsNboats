package com.boats.n.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;

/**
 * Created by ben on 8/25/14.
 */
public class Vector1 implements Vector<Vector1>
{
    public final static Vector1 Zero = new Vector1(0);

    public float x;

    public Vector1(float x)
    {
        this.x = x;
    }

    @Override
    public Vector1 cpy()
    {
        return new Vector1(x);
    }

    @Override
    public float len()
    {
        return x;
    }

    @Override
    public float len2()
    {
        return x*x;
    }

    @Override
    public Vector1 limit(float limit)
    {
        if (x > limit)
        {
            x = limit;
        }
        return this;
    }

    @Override
    public Vector1 clamp(float min, float max)
    {
        if (x > max)
        {
            x = max;
        }
        if (x < min)
        {
            x = min;
        }
        return this;
    }

    @Override
    public Vector1 set(Vector1 v)
    {
        x = v.x;
        return this;
    }

    @Override
    public Vector1 sub(Vector1 v)
    {
        return null;
    }

    @Override
    public Vector1 nor()
    {
        x = 1;
        return this;
    }

    @Override
    public Vector1 add(Vector1 v)
    {
        x += v.x;
        return this;
    }

    @Override
    public float dot(Vector1 v)
    {
        return v.x * x;
    }

    @Override
    public Vector1 scl(float scalar)
    {
        x *= scalar;
        return this;
    }

    @Override
    public Vector1 scl(Vector1 v)
    {
        x *= v.x;
        return this;
    }

    @Override
    public float dst(Vector1 v)
    {
        return Math.abs(x - v.x);
    }

    @Override
    public float dst2(Vector1 v)
    {
        return (x - v.x) * (x - v.x);
    }

    @Override
    public Vector1 lerp(Vector1 target, float alpha)
    {
        x = x * (1 - alpha) + target.x * alpha;
        return this;
    }

    @Override
    public Vector1 interpolate(Vector1 target, float alpha, Interpolation interpolator)
    {
        return lerp(target, interpolator.apply(0f, 1f, alpha));
    }

    @Override
    public boolean isUnit()
    {
        return x == 1;
    }

    @Override
    public boolean isUnit(float margin)
    {
        return Math.abs(x - 1) <= margin;
    }

    @Override
    public boolean isZero()
    {
        return MathUtils.isZero(x);
    }

    @Override
    public boolean isZero(float margin)
    {
        return Math.abs(x) < margin;
    }

    @Override
    public boolean isOnLine(Vector1 other, float epsilon)
    {
        return true;
    }

    @Override
    public boolean isOnLine(Vector1 other)
    {
        return true;
    }

    @Override
    public boolean isCollinear(Vector1 other, float epsilon)
    {
        return true;
    }

    @Override
    public boolean isCollinear(Vector1 other)
    {
        return true;
    }

    @Override
    public boolean isCollinearOpposite(Vector1 other, float epsilon)
    {
        return true;
    }

    @Override
    public boolean isCollinearOpposite(Vector1 other)
    {
        return true;
    }

    @Override
    public boolean isPerpendicular(Vector1 other)
    {
        return false;
    }

    @Override
    public boolean isPerpendicular(Vector1 other, float epsilon)
    {
        return false;
    }

    @Override
    public boolean hasSameDirection(Vector1 other)
    {
        return Math.signum(x) == Math.signum(other.x);
    }

    @Override
    public boolean hasOppositeDirection(Vector1 other)
    {
        return !hasSameDirection(other);
    }

    @Override
    public boolean epsilonEquals(Vector1 other, float epsilon)
    {
        return MathUtils.isEqual(x, other.x, epsilon);
    }

    @Override
    public Vector1 mulAdd(Vector1 v, float scalar)
    {
        x += v.x * scalar;
        return this;
    }

    @Override
    public Vector1 mulAdd(Vector1 v, Vector1 mulVec)
    {
        x += v.x * mulVec.x;
        return this;
    }

    @Override
    public Vector1 setZero()
    {
        x = 0;
        return this;
    }
}
