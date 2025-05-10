package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerA extends Player {
    private Animation<TextureRegion> animation;
    private float stateTime;
    private boolean hasKey;
    private static final float PLAYER_WIDTH = 64;
    private static final float PLAYER_HEIGHT = 64;

    public PlayerA(World world, float x, float y, Animation<TextureRegion> animation) {
        super(world, x, y, 5, 2000f, true);  // Knight character
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
        
        // Output speed for debugging
        
        // Handle input for horizontal movement with direct speed parameter
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            // Use the speed parameter directly - which is 2000f based on constructor
            desiredVelX = -speed * 0.05f; // Scale down slightly to make it manageable
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            // Use the speed parameter directly - which is 2000f based on constructor
            desiredVelX = speed * 0.05f; // Scale down slightly to make it manageable
            isMoving = true;
        }
        
        // Handle jumping with W
        if (Gdx.input.isKeyPressed(Input.Keys.W) && canJump) {
            desiredVelY = speed * 0.1f; // Scale jump with speed
            canJump = false; // Can't jump again until touching ground
        }
        
        // If on wall and not on ground, apply wall sliding
        if (onWall && !canJump && currentVel.y < 0) {
            body.setLinearVelocity(currentVel.x, -2f); // Slow falling speed when against wall
        }
        
        // Apply movement forces with immediate velocity - bypass physics calculations
        if (isMoving) {
            // Directly set the velocity for instant responsive movement
            body.setLinearVelocity(desiredVelX, currentVel.y);
            
            // Update facing direction for animation
            setFacingRight(desiredVelX > 0);
            
            // Set appropriate animation state
            setAnimationState(PlayerAnimation.PlayerState.RUNNING);
        } else {
            // Apply friction/damping when not actively moving (except on ice)
            if (!onIce) {
                body.setLinearVelocity(currentVel.x * 0.8f, currentVel.y);
            }
            
            // Set idle animation if not moving horizontally
            if (Math.abs(currentVel.x) < 0.1f && !isJumping() && !isClimbing) {
                setAnimationState(PlayerAnimation.PlayerState.IDLE);
            }
        }
        
        // Apply jumping force
        if (desiredVelY > 0) {
            body.setLinearVelocity(currentVel.x, desiredVelY);
            setAnimationState(PlayerAnimation.PlayerState.JUMPING);
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