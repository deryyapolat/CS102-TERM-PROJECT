package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Key extends GameObject {
    private int id;
    private boolean isCollected;
    private boolean active;

    public Key(World world, float x, float y, float width, float height, MapProperties properties) {
        super(world, x, y, width, height);
        
        Object idObj = properties.get("id");
        this.id = (idObj instanceof Integer) ? (Integer)idObj : 
                  (idObj instanceof String) ? Integer.parseInt((String)idObj) : 1;
                  
        this.isCollected = false;
        this.active = true; // Initialize active state
    }

    @Override
    public void update(float delta) {
        // Key state is changed externally when collected
    }

    public void collect() {
        this.isCollected = true;
        this.active = false; // Deactivate collision once collected
    }

    public boolean isCollected() {
        return isCollected;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}