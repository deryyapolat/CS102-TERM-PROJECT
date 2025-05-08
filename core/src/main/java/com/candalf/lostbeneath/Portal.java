package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.maps.MapProperties;

public class Portal extends GameObject {
    private Rectangle bounds;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private int id;
    private boolean isOpen;
    private String targetMap;
    private float targetX, targetY;

    public Portal(World world, float x, float y, float width, float height, MapProperties props) {
        super(world, x, y, width, height);
        
        this.bounds = new Rectangle(x, y, width, height);
        this.stateTime = 0;
        
        // Get portal properties
        Object idObj = props.get("id");
        this.id = idObj != null ? Integer.parseInt(idObj.toString()) : 0;
        
        Object targetMapObj = props.get("targetMap");
        this.targetMap = targetMapObj != null ? targetMapObj.toString() : "";
        
        Object targetXObj = props.get("targetX");
        this.targetX = targetXObj != null ? Float.parseFloat(targetXObj.toString()) : 0f;
        
        Object targetYObj = props.get("targetY");
        this.targetY = targetYObj != null ? Float.parseFloat(targetYObj.toString()) : 0f;
        
        // Create Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width/2, y + height/2);
        
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;  // Portal doesn't block movement
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        Vector2 pos = body.getPosition();
        bounds.setPosition(pos.x - bounds.width/2, pos.y - bounds.height/2);
    }
    
    public void render(SpriteBatch batch) {
        if (animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    public int getId() {
        return id;
    }
    
    public void setOpen(boolean open) {
        this.isOpen = open;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public String getTargetMap() {
        return targetMap;
    }
    
    public float getTargetX() {
        return targetX;
    }
    
    public float getTargetY() {
        return targetY;
    }
}