package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Heal extends GameObject {
    private Rectangle bounds;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private boolean collected;
    private int healAmount;
    private static TextureRegion defaultTexture; // Static texture for all health pickups
    
    public Heal(World world, float x, float y, float width, float height, int healAmount) {
        super(world, x, y, width, height);
        
        this.healAmount = healAmount;
        this.collected = false;
        this.stateTime = 0;
        
        // Initialize texture if not already loaded
        if (defaultTexture == null) {
            try {
                // Try to use an existing health potion or heart image
                com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture("düzenlenen itemler/key.png");
                defaultTexture = new TextureRegion(texture);
            } catch (Exception e) {
                System.err.println("Error loading health pickup texture: " + e.getMessage());
                // Create a simple colored texture as fallback
                com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                pixmap.setColor(com.badlogic.gdx.graphics.Color.RED);
                pixmap.fillCircle(16, 16, 12);
                com.badlogic.gdx.graphics.Texture fallbackTexture = new com.badlogic.gdx.graphics.Texture(pixmap);
                defaultTexture = new TextureRegion(fallbackTexture);
                pixmap.dispose();
            }
        }
        
        // Create a simple animation with just the default texture
        Array<TextureRegion> frames = new Array<>();
        frames.add(defaultTexture);
        animation = new Animation<>(0.1f, frames);
        
        // Create shrinked bounds and Box2D body
        float shrinkFactor = 0.3f;
        float hitWidth = width * shrinkFactor;
        float hitHeight = height * shrinkFactor;
        float offsetX = (width - hitWidth) / 2f;
        float offsetY = (height - hitHeight) / 2f;
        bounds = new Rectangle(x + offsetX, y + offsetY, hitWidth, hitHeight);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + offsetX + hitWidth / 2f, y + offsetY + hitHeight / 2f);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hitWidth / 2f, hitHeight / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    @Override
    public void update(float delta) {
        // Skip updates for collected pickups or if body is null (destroyed)
        if (collected || body == null) return;
        
        stateTime += delta;
        
        // Update bounds position
        bounds.setPosition(body.getPosition().x - bounds.width/2, 
                         body.getPosition().y - bounds.height/2);
        
        // Add a visual floating effect for better visibility
        float floatOffset = (float) Math.sin(stateTime * 3) * 2f;
        body.setTransform(body.getPosition().x, body.getPosition().y + floatOffset * delta, 0);
    }
    
    public void render(SpriteBatch batch) {
        // ABSOLUTELY NO RENDERING if collected
        if (collected) return;
        
        // Additional checks to ensure we don't render
        if (animation == null || body == null) return;
        
        // Double check the body is active
        if (!body.isActive()) return;
        
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        
        // Add pulsating effect for better visibility
        float scale = 1.0f + (float) Math.sin(stateTime * 2) * 0.1f;
        float width = bounds.width * scale;
        float height = bounds.height * scale;
        float x = bounds.x - (width - bounds.width) / 2;
        float y = bounds.y - (height - bounds.height) / 2;
        
        // Draw the health pickup with original color
        batch.draw(currentFrame, x, y, width, height);
    }
    
    public void collect(Player player) {
        // Check if already collected to prevent double processing
        if (!collected) {
            // Always mark as collected FIRST to prevent any further interactions
            collected = true;

            // Apply healing
            player.heal(healAmount);

            // IMMEDIATELY destroy the body - don't wait for physics step
            if (body != null) {
                try {
                    body.getWorld().destroyBody(body);
                } catch (Exception e) {
                    // Ignore any errors during destruction
                }
                body = null;
            }

            // Force the bounds off-screen
            bounds.setPosition(-99999, -99999);
        }
    }
    
    /**
     * Forces destruction of this health pickup
     */
    public void forceDestroy() {
        if (body != null && !collected) {
            try {
                body.getWorld().destroyBody(body);
                body = null;
                collected = true;
                bounds.setPosition(-99999, -99999);
            } catch (Exception e) {
                // Ignore any errors
            }
        }
        collected = true;
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public Rectangle getBounds() {
        // Make sure bounds is updated to the body position before returning
        if (body != null && !collected) {
            bounds.setPosition(
                body.getPosition().x - bounds.width/2,
                body.getPosition().y - bounds.height/2
            );
        } else if (collected || body == null) {
            // If collected or body is destroyed, ensure bounds are far off-screen
            bounds.setPosition(-10000, -10000);
        }
        return bounds;
    }
    
    public int getHealAmount() {
        return healAmount;
    }
}