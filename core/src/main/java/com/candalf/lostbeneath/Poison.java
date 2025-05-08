package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

public class Poison extends GameObject {
    private int damage;
    private float damageInterval;
    private float damageTimer;
    
    public Poison(World world, float x, float y, float width, float height, MapProperties properties) {
        super(world, x, y, width, height);
        
        // Get properties from map or use defaults
        Object damageObj = properties.get("damage");
        this.damage = (damageObj instanceof Integer) ? (Integer)damageObj : 
                     (damageObj instanceof String) ? Integer.parseInt((String)damageObj) : 1;
        
        Object intervalObj = properties.get("interval");
        this.damageInterval = (intervalObj instanceof Float) ? (Float)intervalObj : 
                             (intervalObj instanceof String) ? Float.parseFloat((String)intervalObj) : 1.0f;
        
        this.damageTimer = 0;
        
        // Make it a sensor since it's just a trigger area
        body.getFixtureList().get(0).setSensor(true);
    }

    @Override
    public void update(float delta) {
        damageTimer += delta;
    }

    public boolean canDealDamage() {
        if (damageTimer >= damageInterval) {
            damageTimer = 0;
            return true;
        }
        return false;
    }

    public int getDamage() {
        return damage;
    }
}