package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Knight-specific player class to completely separate animation handling
 */
public class KnightPlayer extends Player {
    private boolean knightFacingRight = true;
    private PlayerAnimation.PlayerState knightState = PlayerAnimation.PlayerState.IDLE;
    private PlayerAnimation knightAnimation;

    public KnightPlayer(World world, float startX, float startY, int maxHealth, float speed) {
        super(world, startX, startY, maxHealth, speed, true);
        this.knightAnimation = new PlayerAnimation(true); // Knight animation
    }
    
    @Override
    public void update(float delta) {
        // Store current position
        getPreviousPosition().set(body.getPosition());
        
        // Update position from physics body
        position.set(body.getPosition().x - width/2, body.getPosition().y - height/2);
        bounds.setPosition(position.x, position.y);
        
        // Update just the knight animation with knight-specific state
        if (knightAnimation != null) {
            knightAnimation.update(delta, knightState, knightFacingRight);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (knightAnimation != null) {
            batch.draw(knightAnimation.getCurrentFrame(), 
                      position.x, position.y,
                      width, height);
        }
    }
    
    @Override
    public void setFacingRight(boolean isFacingRight) {
        this.knightFacingRight = isFacingRight; 
    }
    
    @Override
    public void setAnimationState(PlayerAnimation.PlayerState state) {
        if (this.knightState != state) {
            this.knightState = state;
            if (knightAnimation != null) {
                knightAnimation.resetStateTime();
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (knightAnimation != null) {
            knightAnimation.dispose();
        }
    }
}