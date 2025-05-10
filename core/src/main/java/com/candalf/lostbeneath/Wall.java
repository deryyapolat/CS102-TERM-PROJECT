package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Wall extends GameObject {
    private Rectangle bounds;
    private static final float WALL_FRICTION = 0.8f;
    
    public Wall(World world, float x, float y, float width, float height, MapProperties props) {
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
        fixtureDef.friction = WALL_FRICTION;  // High friction for walls
        fixtureDef.isSensor = false;  // Make it a physical barrier, not just a sensor
        fixtureDef.restitution = 0.0f; // No bouncing
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    // Simple constructor without properties
    public Wall(World world, float x, float y, float width, float height) {
        this(world, x, y, width, height, new MapProperties());
    }
    
    @Override
    public void update(float delta) {
        // Update bounds position if needed
        bounds.setPosition(body.getPosition().x - bounds.width/2, 
                         body.getPosition().y - bounds.height/2);
    }
    
    public void applyWallEffect(Player player) {
        // Get player position and wall bounds to determine spatial relationship
        Vector2 playerPos = player.getBody().getPosition();
        float playerBottom = playerPos.y - player.getBounds().height/2;
        float wallTop = bounds.y + bounds.height;
        
        // Check if player is standing on top of the wall or beside it
        boolean isOnTop = (playerBottom >= wallTop - 5f); // Player is on top if feet are near or above the wall top
        
        if (isOnTop) {
            // Player is on top of the wall, treat as ground
            player.setCanJump(true);
        } else {
            // Player is beside the wall, enable wall sliding/jumping
            Body playerBody = player.getBody();
            Vector2 vel = playerBody.getLinearVelocity();
            
            // Enable jumping when on wall
            player.setCanJump(true);
            
            // Allow sliding down walls much faster, but still with some control
            if (vel.y < 0) {
                // Apply wall sliding speed (faster than before, but not as fast as free-falling)
                playerBody.setLinearVelocity(vel.x, -15f); // Fast wall sliding speed matching the increased gravity
            }
        }
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
}