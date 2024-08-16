package com.systemvi.engine.physics2d;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

public class Circle extends PhysicsBody {
    private float r;
    public Circle(World world,float x,float y,float r){
        BodyDef bodyDef=new BodyDef();
        bodyDef.position.set(x,y);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.linearDamping=0;
        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.density=1;
        fixtureDef.restitution=0.5f;
        fixtureDef.friction=0.7f;
        CircleShape shape=new CircleShape();
        this.r=r;
        shape.setRadius(r);

        fixtureDef.shape=shape;

        Body body=world.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    @Override
    public void debugDraw(ShapeRenderer renderer) {

    }
}
