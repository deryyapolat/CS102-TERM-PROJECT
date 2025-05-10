package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerB extends Player {
    private Animation<TextureRegion> animation;
    private float stateTime;
    private boolean hasKey;
    private static final float PLAYER_WIDTH = 64;
    private static final float PLAYER_HEIGHT = 64;

    public PlayerB(World world, float x, float y, Animation<TextureRegion> animation) {
        super(world, x, y, 5, 2000f, false);  // Mage character
        this.animation = animation;
        this.stateTime = 0;
        
        // Set up Box2D body properties
        body.setFixedRotation(true);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        
        // Get current velocity from the body
        Vector2 currentVel = body.getLinearVelocity();
        boolean isMoving = false;
        float desiredVelX = 0;
        float desiredVelY = 0;
        
        // Handle input for horizontal movement
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            desiredVelX = -speed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            desiredVelX = speed;
            isMoving = true;
        }
        
        // Handle jumping with UP key
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && canJump) {
            desiredVelY = speed * 2.5f; // Apply jump force
            canJump = false; // Can't jump again until touching ground
        }
        
        // If on wall and not on ground, apply wall sliding
        if (onWall && !canJump && currentVel.y < 0) {
            body.setLinearVelocity(currentVel.x, -2f); // Slow falling speed when against wall
        }
        
        // Apply movement forces
        if (isMoving) {
            float velChange = desiredVelX - currentVel.x;
            float force = body.getMass() * velChange / delta;
            
            // Apply reduced force on ice surfaces
            if (onIce) {
                force *= 0.3f;
            }
            
            body.applyForceToCenter(new Vector2(force, 0), true);
        } else {
            // Apply friction/damping when not actively moving (except on ice)
            if (!onIce) {
                body.setLinearVelocity(currentVel.x * 0.8f, currentVel.y);
            }
        }
        
        // Apply jumping force
        if (desiredVelY > 0) {
            body.setLinearVelocity(currentVel.x, desiredVelY);
        }
        
        // Call the parent update to handle position updates and animations
        super.update(delta);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        Vector2 pos = body.getPosition();
        batch.draw(currentFrame, pos.x - PLAYER_WIDTH/2, pos.y - PLAYER_HEIGHT/2, 
                  PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public void heal(int amount) {
        super.heal(amount);
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public void damage(int amount) {
        super.damage(amount);
    }

    public void setOnIce(boolean onIce) {
        super.onIce = onIce; // Properly set the parent class's onIce property
    }
}