package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class Rope extends GameObject {
    private Rectangle bounds;
    
    public Rope(World world, float x, float y, float width, float height) {
        super(world, x, y, width, height);
        
        // Create bounds for collision detection
        bounds = new Rectangle(x, y, width, height);
        
        // Create Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width/2, y + height/2);
        
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;  // Make it a sensor to allow passing through
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    @Override
    public void update(float delta) {
        bounds.setPosition(body.getPosition().x - bounds.width/2, 
                         body.getPosition().y - bounds.height/2);
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
}