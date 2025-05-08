package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

public class Button extends GameObject {
    private int id;
    private int targetId;
    private boolean isPressed;
    private float cooldownTime;
    private float timeElapsed;

    public Button(World world, float x, float y, float width, float height, MapProperties properties) {
        super(world, x, y, width, height);
        
        Object idObj = properties.get("id");
        this.id = (idObj instanceof Integer) ? (Integer)idObj : 
                  (idObj instanceof String) ? Integer.parseInt((String)idObj) : 1;
                  
        Object targetIdObj = properties.get("targetId");
        this.targetId = (targetIdObj instanceof Integer) ? (Integer)targetIdObj :
                       (targetIdObj instanceof String) ? Integer.parseInt((String)targetIdObj) : 1;
                       
        this.isPressed = false;
        this.cooldownTime = 0.5f; // Half second cooldown
        this.timeElapsed = cooldownTime;
    }

    @Override
    public void update(float delta) {
        if (isPressed) {
            timeElapsed += delta;
            if (timeElapsed >= cooldownTime) {
                isPressed = false;
                timeElapsed = 0;
            }
        }
    }

    public void press() {
        if (!isPressed && timeElapsed >= cooldownTime) {
            isPressed = true;
            timeElapsed = 0;
        }
    }

    public boolean isPressed() {
        return isPressed;
    }

    public int getId() {
        return id;
    }

    public int getTargetId() {
        return targetId;
    }
}