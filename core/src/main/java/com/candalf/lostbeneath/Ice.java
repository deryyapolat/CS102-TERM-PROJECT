package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Ice extends GameObject {
    private Rectangle bounds;
    private float slipperiness;
    private static final float DEFAULT_SLIPPERINESS = 0.95f;
    private static final float ICE_FRICTION = 0.05f;
    
    public Ice(World world, float x, float y, float width, float height, MapProperties props) {
        super(world, x, y, width, height);
        
        // Create bounds for collision detection
        bounds = new Rectangle(x, y, width, height);
        
        // Get slipperiness from properties or use default
        Object slipObj = props.get("slipperiness");
        this.slipperiness = slipObj != null ? Float.parseFloat(slipObj.toString()) : DEFAULT_SLIPPERINESS;
        
        // Create Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width/2, y + height/2);
        
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = ICE_FRICTION;  // Very low friction for ice
        fixtureDef.isSensor = false;  // Make it a physical barrier, not just a sensor
        fixtureDef.restitution = 0.0f; // No bouncing
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    @Override
    public void update(float delta) {
        // Update bounds position if needed
        bounds.setPosition(body.getPosition().x - bounds.width/2, 
                         body.getPosition().y - bounds.height/2);
    }
    
    public void applyIceEffect(Player player) {
        Body playerBody = player.getBody();
        Vector2 vel = playerBody.getLinearVelocity();
        
        // Only apply ice effect if player is moving
        if (vel.len2() > 0.1f) {
            // Calculate ice physics - maintain momentum with slight decay
            float xForce = vel.x * slipperiness;
            
            // Clear any existing vertical velocity to prevent flying
            playerBody.setLinearVelocity(xForce, vel.y);
            
            // Apply a small impulse in the direction of movement to maintain momentum
            playerBody.applyLinearImpulse(
                new Vector2(xForce * 0.1f, 0),
                playerBody.getWorldCenter(),
                true
            );
        }
        
        player.setOnIce(true);
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
}