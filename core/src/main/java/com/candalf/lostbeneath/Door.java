package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Door extends GameObject {
    private int requiredKeyId;
    private boolean isOpen;
    private boolean locked;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private float x, y, width, height;
    private boolean active;
    private Rectangle bounds;

    public Door(World world, float x, float y, float width, float height, MapProperties properties) {
        super(world, x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;

        Object keyIdObj = properties.get("required_key_id");
        this.requiredKeyId = (keyIdObj instanceof Integer) ? (Integer)keyIdObj : 
                            (keyIdObj instanceof String) ? Integer.parseInt((String)keyIdObj) : 1;

        this.isOpen = false;
        this.locked = true;
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
        fixtureDef.isSensor = false;  // Door blocks movement when locked
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        bounds.setPosition(body.getPosition().x - bounds.width/2, 
                         body.getPosition().y - bounds.height/2);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, width, height);
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean tryUnlock(Key key) {
        if (key != null && key.getId() == requiredKeyId && !key.isCollected()) {
            isOpen = true;
            this.active = false; // Deactivate collision when door is open
            return true;
        }
        return false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getRequiredKeyId() {
        return requiredKeyId;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        locked = false;
        // Make the door a sensor when unlocked so players can pass through
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
    }
}