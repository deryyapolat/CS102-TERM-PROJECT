package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Mage-specific player class to completely separate animation handling
 */
public class MagePlayer extends Player {
    private boolean mageFacingRight = true;
    private PlayerAnimation.PlayerState mageState = PlayerAnimation.PlayerState.IDLE;
    private PlayerAnimation mageAnimation;

    public MagePlayer(World world, float startX, float startY, int maxHealth, float speed) {
        super(world, startX, startY, maxHealth, speed, false);
        this.mageAnimation = new PlayerAnimation(false); // Mage animation
    }
    
    @Override
    public void update(float delta) {
        // Store current position
        getPreviousPosition().set(body.getPosition());
        
        // Update position from physics body
        position.set(body.getPosition().x - width/2, body.getPosition().y - height/2);
        bounds.setPosition(position.x, position.y);
        
        // Update just the mage animation with mage-specific state
        if (mageAnimation != null) {
            mageAnimation.update(delta, mageState, mageFacingRight);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (mageAnimation != null) {
            batch.draw(mageAnimation.getCurrentFrame(), 
                      position.x, position.y,
                      width, height);
        }
    }
    
    @Override
    public void setFacingRight(boolean isFacingRight) {
        this.mageFacingRight = isFacingRight;
    }
    
    @Override
    public void setAnimationState(PlayerAnimation.PlayerState state) {
        if (this.mageState != state) {
            this.mageState = state;
            if (mageAnimation != null) {
                mageAnimation.resetStateTime();
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (mageAnimation != null) {
            mageAnimation.dispose();
        }
    }
}