package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class Heal extends GameObject {
    private Rectangle bounds;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private boolean collected;
    private int healAmount;
    
    public Heal(World world, float x, float y, float width, float height, int healAmount) {
        super(world, x, y, width, height);
        
        this.healAmount = healAmount;
        this.collected = false;
        this.stateTime = 0;
        
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
        fixtureDef.isSensor = true;  // Make it a sensor so players can pass through
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    @Override
    public void update(float delta) {
        stateTime += delta;
        // Update bounds position
        bounds.setPosition(body.getPosition().x - bounds.width/2, 
                         body.getPosition().y - bounds.height/2);
    }
    
    public void render(SpriteBatch batch) {
        if (!collected && animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    public void collect(Player player) {
        if (!collected) {
            player.heal(healAmount);
            collected = true;
            // Make the body a sensor so it doesn't interact with anything
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setSensor(true);
            }
        }
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public int getHealAmount() {
        return healAmount;
    }
}