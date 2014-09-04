package com.boats.n.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsWorld {
    private final World world;

    private static final float BOAT_FRICTION = 0.5f;

    public PhysicsWorld()
    {
        world = new World(Vector2.Zero, false);
    }

    public void step(float dt) {
        world.step(dt, 4, 4);
    }

    public Body createBoat(Vector2 pos, float radius, float density, float restitution) {
        return createCircle(BodyType.DynamicBody, pos, radius, density, restitution, BOAT_FRICTION);
    }

    private Body createCircle(BodyType type, Vector2 pos, float radius, float density, float restitution, float friction) {
      BodyDef def = new BodyDef();
      def.type = type;
      Body box = world.createBody(def);

      CircleShape circle = new CircleShape();
      circle.setPosition(pos);
      circle.setRadius(radius);

      FixtureDef fixtureDef = new FixtureDef();
      fixtureDef.density = density;
      fixtureDef.restitution = restitution;
      fixtureDef.friction = friction;
      fixtureDef.shape = circle;

      box.createFixture(fixtureDef);

      circle.dispose();

      box.setBullet(true);

      return box;
  }
}
