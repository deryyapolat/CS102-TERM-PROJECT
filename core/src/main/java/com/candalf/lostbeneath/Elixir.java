package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

public class Elixir extends GameObject{
    private int id;
    private boolean isCollected;
    private boolean active;

    public Elixir(World world, float x, float y, float width, float height, MapProperties properties) {
        super(world, x, y, width, height);
        setPosition(x, y);

        Object idObj = properties.get("id");
        this.id = (idObj instanceof Integer) ? (Integer)idObj :
                (idObj instanceof String) ? Integer.parseInt((String)idObj) : 1;

        this.isCollected = false;
        this.active = true;
    }

    @Override
    public void update(float delta) {
    }

    public void collect() {
        this.isCollected = true;
        this.active = false;
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
