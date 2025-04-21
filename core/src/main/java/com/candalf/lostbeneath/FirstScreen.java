package com.candalf.lostbeneath; // <-- update to match your package

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class FirstScreen implements Screen {

    private Main game = new Main();
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    // Player positions
    private float player1X = 100, player1Y = 100;
    private float player2X = 300, player2Y = 100;

    private final float speed = 500f; // pixels per second

    public FirstScreen() {
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        handleInput(delta);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Player 1 - Red
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(player1X, player1Y, 32, 32);

        // Player 2 - Blue
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(player2X, player2Y, 32, 32);

        shapeRenderer.end();
    }

    private void handleInput(float delta) {
        float moveAmount = speed * delta;

        // Player 1: WASD
        if (Gdx.input.isKeyPressed(Input.Keys.W)) player1Y += moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player1Y -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player1X -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player1X += moveAmount;

        // Player 2: Arrow Keys
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) player2Y += moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) player2Y -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player2X -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player2X += moveAmount;
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        shapeRenderer.dispose();
    }
}
