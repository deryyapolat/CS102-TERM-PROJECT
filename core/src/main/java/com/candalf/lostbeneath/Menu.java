package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Menu implements Screen {
    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture background;
    private ImageButton startButton;
    private ImageButton optionsButton;
    private ImageButton creditsButton;

    public Menu(Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new FitViewport(1280, 720));
        
        loadAssets();
        createButtons();
        
        Gdx.input.setInputProcessor(stage);
    }

    private void loadAssets() {
        background = new Texture(Gdx.files.internal("ui/mmbackground.png"));
        
        // Load button textures
        Texture startUpTexture = new Texture(Gdx.files.internal("ui/buttons/startup.png"));
        Texture startDownTexture = new Texture(Gdx.files.internal("ui/buttons/startdown.png"));
        Texture optionsUpTexture = new Texture(Gdx.files.internal("ui/buttons/optionsup.png"));
        Texture optionsDownTexture = new Texture(Gdx.files.internal("ui/buttons/optionsdown.png"));
        Texture creditsUpTexture = new Texture(Gdx.files.internal("ui/buttons/creditsup.png"));
        Texture creditsDownTexture = new Texture(Gdx.files.internal("ui/buttons/creditsdown.png"));

        // Create buttons
        ImageButton.ImageButtonStyle startStyle = new ImageButton.ImageButtonStyle();
        startStyle.up = new TextureRegionDrawable(startUpTexture);
        startStyle.down = new TextureRegionDrawable(startDownTexture);
        startButton = new ImageButton(startStyle);

        ImageButton.ImageButtonStyle optionsStyle = new ImageButton.ImageButtonStyle();
        optionsStyle.up = new TextureRegionDrawable(optionsUpTexture);
        optionsStyle.down = new TextureRegionDrawable(optionsDownTexture);
        optionsButton = new ImageButton(optionsStyle);

        ImageButton.ImageButtonStyle creditsStyle = new ImageButton.ImageButtonStyle();
        creditsStyle.up = new TextureRegionDrawable(creditsUpTexture);
        creditsStyle.down = new TextureRegionDrawable(creditsDownTexture);
        creditsButton = new ImageButton(creditsStyle);
    }

    private void createButtons() {
        // Position buttons
        startButton.setSize(272, 57);
        optionsButton.setSize(250, 55);
        creditsButton.setSize(250, 55);

        startButton.setPosition(504, 360);
        optionsButton.setPosition(100, 85);
        creditsButton.setPosition(930, 85);

        // Add listeners
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GamePanel(game));
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement options screen
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement credits screen
            }
        });

        // Add buttons to stage
        stage.addActor(startButton);
        stage.addActor(optionsButton);
        stage.addActor(creditsButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, 1280, 720);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        background.dispose();
    }
}