package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class HealthPickup {
    private float x, y;
    private float width, height;
    private int healAmount;
    private boolean collected;
    private Animation<TextureRegion> animation;
    private float stateTime;
    
    public HealthPickup(float x, float y, float width, float height, int healAmount) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.healAmount = healAmount;
        this.collected = false;
        this.stateTime = 0f;
        loadAnimation();
    }
    
    private void loadAnimation() {
        try {
            // Load health pickup texture (e.g., a heart or potion)
            Texture texture = new Texture(Gdx.files.internal("assets/health_pickup.png"));
            TextureRegion[][] tmp = TextureRegion.split(texture, 
                texture.getWidth() / 4,  // Assuming 4 frames horizontally
                texture.getHeight());
            
            TextureRegion[] frames = new TextureRegion[4];
            System.arraycopy(tmp[0], 0, frames, 0, 4);
            
            animation = new Animation<>(0.25f, frames);
        } catch (Exception e) {
            Gdx.app.error("HealthPickup", "Error loading health pickup animation", e);
        }
    }
    
    public void update(float delta) {
        if (!collected) {
            stateTime += delta;
        }
    }
    
    public void render(SpriteBatch batch) {
        if (!collected && animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, x, y, width, height);
        }
    }
    
    public void checkCollision(Player player) {
        if (!collected && player != null) {
            Rectangle playerBounds = new Rectangle(player.getX() - 32, player.getY() - 32, 64, 64);
            Rectangle pickupBounds = new Rectangle(x, y, width, height);
            
            if (playerBounds.overlaps(pickupBounds)) {
                collected = true;
                player.heal(healAmount);
            }
        }
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public void dispose() {
        if (animation != null && animation.getKeyFrame(0) != null) {
            animation.getKeyFrame(0).getTexture().dispose();
        }
    }
}