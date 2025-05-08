package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGame extends Game {
    public SpriteBatch batch;
    public GameWorld gameWorld;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        gameWorld = new GameWorld();
        setScreen(gameWorld);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        gameWorld.dispose();
    }
}
