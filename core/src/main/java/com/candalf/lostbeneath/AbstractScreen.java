package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScreen extends ScreenAdapter
{
    protected final Game game;
    protected final OrthographicCamera camera;
    protected final FitViewport viewport;
    protected final SpriteBatch batch;
    protected final Stage stage;

    private final int WIDTH = 960;
    private final int HEIGHT = 512;

    public AbstractScreen(Game game)
    {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WIDTH, HEIGHT, camera);
        this.batch = new SpriteBatch();
        this.stage = new Stage(viewport, batch);

        Gdx.input.setInputProcessor(stage);
        initialize();
    }

    protected abstract void initialize();
    protected abstract void update(float delta);
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    @Override
    public void pause()
    {
        // override if you need to handle pause
    }

    @Override
    public void hide()
    {
        dispose();
    }

    @Override
    public void dispose()
    {
        stage.dispose();
        batch.dispose();
    }

}
