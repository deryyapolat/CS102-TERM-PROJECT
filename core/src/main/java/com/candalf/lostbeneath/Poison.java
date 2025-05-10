package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Poison extends GameObject {
    private int damage;
    private float damageInterval;
    private float damageTimer;
    private Rectangle bounds; // Add bounds for collision detection
    // Visual effect properties (optional)
    private float alphaValue = 0.6f;
    private com.badlogic.gdx.graphics.Color poisonColor = new com.badlogic.gdx.graphics.Color(0.2f, 0.9f, 0.2f, 0.6f);
    
    /**
     * Constructor that uses default values for damage and interval
     */
    public Poison(World world, float x, float y, float width, float height) {
        this(world, x, y, width, height, new MapProperties());
    }
    
    public Poison(World world, float x, float y, float width, float height, MapProperties properties) {
        super(world, x, y, width, height);
        
        // Initialize bounds rectangle
        bounds = new Rectangle(x, y, width, height);
        
        // Get properties from map or use defaults
        Object damageObj = properties.get("damage");
        this.damage = (damageObj instanceof Integer) ? (Integer)damageObj : 
                     (damageObj instanceof String) ? Integer.parseInt((String)damageObj) : 1;
        
        Object intervalObj = properties.get("interval");
        this.damageInterval = (intervalObj instanceof Float) ? (Float)intervalObj : 
                             (intervalObj instanceof String) ? Float.parseFloat((String)intervalObj) : 1.0f;
        
        // Parse optional visual properties
       
        
        // Parse color properties if specified (format: r,g,b)
        
        this.damageTimer = 0;
        
        // Create Box2D body
        com.badlogic.gdx.physics.box2d.BodyDef bodyDef = new com.badlogic.gdx.physics.box2d.BodyDef();
        bodyDef.type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width/2, y + height/2);
        
        body = world.createBody(bodyDef);
        
        // Create fixture for the body
        com.badlogic.gdx.physics.box2d.PolygonShape shape = new com.badlogic.gdx.physics.box2d.PolygonShape();
        shape.setAsBox(width/2, height/2);
        
        com.badlogic.gdx.physics.box2d.FixtureDef fixtureDef = new com.badlogic.gdx.physics.box2d.FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;  // Make it a sensor since it's just a trigger area
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        // Set user data for collision detection
        body.setUserData(this);
        body.getFixtureList().get(0).setUserData("poison");
    }

    @Override
    public void update(float delta) {
        damageTimer += delta;
        
        // Update bounds position
        if (bounds != null && body != null) {
            bounds.setPosition(body.getPosition().x - bounds.width/2, 
                             body.getPosition().y - bounds.height/2);
        }
        
        // Animate poison color (subtle pulsing effect)
        if (poisonColor != null) {
            // Make alpha value pulse slightly for a bubbling effect
            float pulseAmount = (float)(Math.sin(damageTimer * 3) * 0.1f);
            poisonColor.a = alphaValue + pulseAmount;
        }
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        // Skip rendering if bounds or poisonColor is not initialized
        if (bounds == null || poisonColor == null) return;
        
        // Save original batch color
        com.badlogic.gdx.graphics.Color originalColor = batch.getColor().cpy();
        
        // Set poison color with transparency
        batch.setColor(poisonColor);
        
        // Get the shared white pixel texture or create one if needed
        com.badlogic.gdx.graphics.Texture pixelTexture = getSharedPixelTexture();
        
        // Draw a colored rectangle
        batch.draw(pixelTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Restore original batch color
        batch.setColor(originalColor);
    }
    
    // Shared white pixel texture for simple shape rendering
    private static com.badlogic.gdx.graphics.Texture sharedPixelTexture;
    
    private com.badlogic.gdx.graphics.Texture getSharedPixelTexture() {
        if (sharedPixelTexture == null) {
            // Create a 1x1 white texture for drawing shapes
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 1, 1); // White
            pixmap.fill();
            sharedPixelTexture = new com.badlogic.gdx.graphics.Texture(pixmap);
            pixmap.dispose();
        }
        return sharedPixelTexture;
    }

    public boolean canDealDamage() {
        if (damageTimer >= damageInterval) {
            damageTimer = 0; // Reset timer
            return true;
        }
        return false;
    }

    public int getDamage() {
        return damage;
    }
    
    /**
     * Returns the bounds rectangle for collision detection
     */
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void dispose() {
        // Dispose of any resources
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
        
        // Since sharedPixelTexture is static, we don't dispose it here
        // It will be managed by a static dispose method
    }
    
    /**
     * Static method to dispose of shared resources when the game exits
     */
    public static void disposeShared() {
        if (sharedPixelTexture != null) {
            sharedPixelTexture.dispose();
            sharedPixelTexture = null;
        }
    }
}