package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public abstract class GameObject implements Disposable {
    protected float width;
    protected float height;
    protected Body body;
    
    public GameObject(World world, float x, float y, float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public abstract void update(float delta);
    
    public void render(SpriteBatch batch) {
        // Override in subclasses if needed
    }
    
    public Body getBody() {
        return body;
    }
    
    public float getX() {
        return body != null ? body.getPosition().x : 0;
    }
    
    public float getY() {
        return body != null ? body.getPosition().y : 0;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    @Override
    public void dispose() {
        if (body != null && body.getWorld() != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
    }
}