package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuScreen extends AbstractScreen
{
    private Texture backgroundTexture;
    private ImageButton startButton;
    private ImageButton optionsButton;
    private ImageButton creditsButton;
    private Window optionsWindow;
    private boolean isOptionsOn = false;
    private BitmapFont font;
    private Group optionsOverlay;
    private Table optionsTable;

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

        optionsOverlay = new Group();
        optionsOverlay.setVisible(false);       // start hidden
        stage.addActor(optionsOverlay);

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                optionsOverlay.setVisible(!optionsOverlay.isVisible());
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new CreditsScreen((Game) game));
            }
        });

        optionsTable = new Table();
        optionsTable.setPosition(
                optionsButton.getX() + 80,
                optionsButton.getY() + 40 + optionsButton.getHeight() + 20
        );
        optionsOverlay.addActor(optionsTable);

        // Option texts + icon paths + click actions
        String[] texts = { "LEADERBOARD", "VOLUME", "KEY SWAP", "STORYLINE" };
        String[] icons = {
                "ui/options/leaderboardicon.png",
                "ui/options/volumeicon.png",
                "ui/options/keyswapicon.png",
                "ui/options/storylineicon.png"
        };

        // Loop to create each row
        for (int i = 0; i < texts.length; i++) {
            final int idx = i;

            // 1) Row container
            Table row = new Table();
            row.setBackground((Drawable) null); // transparent background

            // 2) Bullet Label
            Label.LabelStyle ls = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
            Label bullet = new Label("• " + texts[i], ls);

            // 3) Icon Image
            Texture tex = new Texture(Gdx.files.internal(icons[i]));
            Image icon = new Image(tex);
            icon.setSize(12,16);

            // 4) Assemble row
            row.add(bullet).padRight(8);
            row.add(icon).size(14,16);
            row.pack();

            // 5) Add click listener to row
            row.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    switch (idx) {
                        //case 0: showLeaderboard();  break;
                        //case 1: toggleVolume();     break;
                        //case 2: swapKeys();         break;
                        //case 3: showStoryline();    break;
                        case 0: System.out.println("Clicked");  break;

                    }
                }
            });

            // 6) Add row to parent table
            optionsTable.add(row).left().padBottom(6).row();
        }

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
