package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuScreen extends AbstractScreen
{
    private Texture backgroundTexture;
    private ImageButton startButton;
    private ImageButton optionsButton;
    private ImageButton creditsButton;

    public MenuScreen(Main game)
    {
        super(game);
    }

    @Override
    protected void initialize()
    {
        backgroundTexture = new Texture(Gdx.files.internal("ui/mmbackground.png"));


        Texture startup   = new Texture(Gdx.files.internal("ui/buttons/startup.png"));
        Texture startdown = new Texture(Gdx.files.internal("ui/buttons/startdown.png"));


        ImageButton.ImageButtonStyle startStyle = new ImageButton.ImageButtonStyle();
        startStyle.up   = new TextureRegionDrawable(new TextureRegion(startup));
        startStyle.down = new TextureRegionDrawable(new TextureRegion(startdown));


        startButton = new ImageButton(startStyle);

        // 4. Lay it out and add listeners
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                //game.setScreen(new LevelScreen(game));
            }
        });

        Texture optionsup   = new Texture(Gdx.files.internal("ui/buttons/optionsup.png"));
        Texture optionsdown = new Texture(Gdx.files.internal("ui/buttons/optionsdown.png"));


        ImageButton.ImageButtonStyle optionsStyle = new ImageButton.ImageButtonStyle();
        optionsStyle.up   = new TextureRegionDrawable(new TextureRegion(optionsup));
        optionsStyle.down = new TextureRegionDrawable(new TextureRegion(optionsdown));


        optionsButton = new ImageButton(optionsStyle);

        // 4. Lay it out and add listeners, just like before
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                //game.setScreen(new OptionsScreen(game));
            }
        });

        Texture creditsup   = new Texture(Gdx.files.internal("ui/buttons/creditsup.png"));
        Texture creditsdown = new Texture(Gdx.files.internal("ui/buttons/creditsdown.png"));


        ImageButton.ImageButtonStyle creditsStyle = new ImageButton.ImageButtonStyle();
        creditsStyle.up   = new TextureRegionDrawable(new TextureRegion(creditsup));
        creditsStyle.down = new TextureRegionDrawable(new TextureRegion(creditsdown));


        creditsButton = new ImageButton(creditsStyle);


        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                //game.setScreen(new CreditsScreen(game));
            }
        });

        Image bg = new Image(backgroundTexture);
        bg.setFillParent(true);
        stage.addActor(bg);

        startButton.setSize(272, 57);
        optionsButton.setSize(250, 55);
        creditsButton.setSize(250, 55);

        startButton.setPosition(345, 155);
        optionsButton.setPosition(100, 85);
        creditsButton.setPosition(605, 85);

        stage.addActor(startButton);
        stage.addActor(optionsButton);
        stage.addActor(creditsButton);

        //Listeners

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new LevelScreen((Game) game));  // your first level screen
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new OptionsScreen((Game) game));
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new CreditsScreen((Game) game));
            }
        });
    }

    @Override
    protected void update(float delta)
    {
        // handle input or animations specific to the menu
    }

    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
    }
}
