package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;

public class GameWorld implements Screen {
    private World world;
    private static final float GRAVITY = -30f; // Increased gravity for faster fall

    public GameWorld() {
        world = new World(new Vector2(0, GRAVITY), true);
    }

    @Override
    public void render(float delta) {
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        world.dispose();
    }

    // Implementiere die restlichen Screen-Methoden
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
