package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Entity extends GameObject {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float speed;
    protected Rectangle bounds;
    protected boolean onIce;

    public Entity(World world, float x, float y, float width, float height) {
        super(world, x, y, width, height);
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.speed = 200f;  // Default speed
        this.bounds = new Rectangle(x, y, width, height);
        this.onIce = false;
    }

    @Override
    public void update(float delta) {
        // Update position based on Box2D body
        position.set(body.getPosition().x - width/2, body.getPosition().y - height/2);
        bounds.setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x, y);
        body.setTransform(x + width/2, y + height/2, 0);
    }

    public void setPosition(Vector2 pos) {
        setPosition(pos.x, pos.y);
    }

    public boolean collidesWith(GameObject obj) {
        if (obj instanceof Entity) {
            return bounds.overlaps(((Entity)obj).getBounds());
        }
        return false;
    }

    public void setOnIce(boolean onIce) {
        this.onIce = onIce;
    }

    public boolean isOnIce() {
        return onIce;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public Body getBody() {
        return super.getBody();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public abstract void render(SpriteBatch batch);
}