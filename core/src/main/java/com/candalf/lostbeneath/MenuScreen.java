package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuScreen extends AbstractScreen
{
    private Texture backgroundTexture;
    private Skin uiSkin;
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

        // build UI tables, buttons, listeners, etc.
        Texture startup   = new Texture(Gdx.files.internal("ui/buttons/startup.png"));
        Texture startdown = new Texture(Gdx.files.internal("ui/buttons/startdown.png"));

        // 2. Create a Style
        ImageButton.ImageButtonStyle startStyle = new ImageButton.ImageButtonStyle();
        startStyle.up   = new TextureRegionDrawable(new TextureRegion(startup));
        startStyle.down = new TextureRegionDrawable(new TextureRegion(startdown));

        // 3. Instantiate the ImageButton with that style
        startButton = new ImageButton(startStyle);

        // 4. Lay it out and add listeners, just like before
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                //game.setScreen(new LevelScreen(game));
            }
        });

        Texture optionsup   = new Texture(Gdx.files.internal("ui/buttons/optionsup.png"));
        Texture optionsdown = new Texture(Gdx.files.internal("ui/buttons/optionsdown.png"));

        // 2. Create a Style
        ImageButton.ImageButtonStyle optionsStyle = new ImageButton.ImageButtonStyle();
        optionsStyle.up   = new TextureRegionDrawable(new TextureRegion(optionsup));
        optionsStyle.down = new TextureRegionDrawable(new TextureRegion(optionsdown));

        // 3. Instantiate the ImageButton with that style
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

        // 2. Create a Style
        ImageButton.ImageButtonStyle creditsStyle = new ImageButton.ImageButtonStyle();
        creditsStyle.up   = new TextureRegionDrawable(new TextureRegion(creditsup));
        creditsStyle.down = new TextureRegionDrawable(new TextureRegion(creditsdown));

        // 3. Instantiate the ImageButton with that style
        creditsButton = new ImageButton(creditsStyle);

        // 4. Lay it out and add listeners, just like before
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                //game.setScreen(new CreditsScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(startButton).pad(10).width(200).height(50).row();
        table.add(optionsButton).pad(10).width(200).height(50).row();
        table.add(creditsButton).pad(10).width(200).height(50);

        stage.addActor(table);

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
