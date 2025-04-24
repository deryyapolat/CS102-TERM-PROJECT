package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Player {
    private float x, y;
    private Color color;
    private int health;
    private final int maxHealth;
    private float speed;

    public Player(float startX, float startY, Color color, int maxHealth, float speed) {
        this.x = startX;
        this.y = startY;
        this.color = color;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = speed;
    }

    /** Update movement based on input keys. */
    public void update(float delta, int upKey, int downKey, int leftKey, int rightKey) {
        float move = speed * delta;
        if (Gdx.input.isKeyPressed(upKey))    y += move;
        if (Gdx.input.isKeyPressed(downKey))  y -= move;
        if (Gdx.input.isKeyPressed(leftKey))  x -= move;
        if (Gdx.input.isKeyPressed(rightKey)) x += move;
    }

    /** Render the player as a rectangle. */
    public void render(ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.rect(x, y, 32, 32);
    }

    /** Decrease health by 1 (if above zero). */
    public void damage() {
        if (health > 0) health--;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
