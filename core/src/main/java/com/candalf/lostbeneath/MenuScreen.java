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
    private boolean isSoundOn = false;
    private BitmapFont font;
    private Group optionsOverlay;
    private Table optionsTable;
    private Group creditsOverlay;
    private Table creditsTable;
    private String[] optionsIcons;
    private String volumeIcon;
    private Image volumeIconImage;
    private Group leaderboardOverlay;
    private Label.LabelStyle labelStyle;

    public MenuScreen(Main game)
    {
        super(game);

    }

    @Override
    protected void initialize()
    {
        font = new BitmapFont();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        volumeIcon = "ui/options/volumeicon.png";
        optionsIcons = new String[4];
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
                game.setScreen(new GamePanel(game));  // your first level screen
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

        creditsOverlay = new Group();
        creditsOverlay.setVisible(false);       // start hidden
        stage.addActor(creditsOverlay);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                creditsOverlay.setVisible(!creditsOverlay.isVisible());
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
        optionsIcons[0] = "ui/options/leaderboardicon.png";
        optionsIcons[1] = volumeIcon;
        optionsIcons[2] = "ui/options/keyswapicon.png";
        optionsIcons[3] = "ui/options/storylineicon.png";


        // Loop to create each row
        for (int i = 0; i < texts.length; i++)
        {
            final int idx = i;

            // 1) Row container
            Table row = new Table();
            row.setBackground((Drawable) null); // transparent background

            // 2) Bullet Label

            Label bullet = new Label("• " + texts[i], labelStyle);

            // 3) Icon Image
            Texture tex = new Texture(Gdx.files.internal(optionsIcons[i]));
            Image icon = new Image(tex);
            icon.setSize(12,16);

            if (idx == 1) {
                volumeIconImage = icon;
            }

            // 4) Assemble row
            row.add(bullet).padRight(8);
            row.add(icon).size(14,16);
            row.pack();

            // 5) Add click listener to row
            row.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    switch (idx) {
                        case 0: leaderboardWindow(); showLeaderboard();  break;
                        case 1: toggleVolume();     break;
                        //case 2: swapKeysWindow();         break;
                        //case 3: showStoryline();    break;
                    }
                }
            });

            // 6) Add row to parent table
            optionsTable.add(row).left().padBottom(6).row();
        }
            //Credits table

            creditsTable = new Table();
            creditsTable.setPosition(
                    creditsButton.getX() + 100,
                    creditsButton.getY() + 40 + creditsButton.getHeight() + 30
            );
            creditsOverlay.addActor(creditsTable);

            String[] creditsTexts = { "  GAME CREDITS:", "ARAS SOYLU", "ARDA AKIN", "DERYA POLAT", "MEHMET CAN BASTEM", "SINEM YILDIRIM" };

            for (int j = 0; j < creditsTexts.length; j++) {
                // 1) Row container
                Table creditsrow = new Table();
                creditsrow.setBackground((Drawable) null); // transparent background

                // 2) Bullet Label
                Label cbullet;
                if (j != 0) {
                     cbullet = new Label("• " + creditsTexts[j], labelStyle);
                }
                else
                {
                     cbullet = new Label(creditsTexts[j], labelStyle);
                }
                creditsrow.add(cbullet).padRight(8);
                creditsrow.pack();
                creditsTable.add(creditsrow).left().padBottom(6).row();
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

    private void toggleVolume()
    {
        if(isSoundOn)
        {
            volumeIcon = "ui/options/soundofficon.png";
            isSoundOn = false;
        }
        else
        {
            volumeIcon = "ui/options/volumeicon.png";
            isSoundOn = true;
        }
        if (volumeIconImage != null) {
            Texture newTex = new Texture(Gdx.files.internal(volumeIcon));
            volumeIconImage.setDrawable(new TextureRegionDrawable(new TextureRegion(newTex)));
        }
    }
    private void leaderboardWindow()
    {
        if (leaderboardOverlay != null) return;

        leaderboardOverlay = new Group();
        stage.addActor(leaderboardOverlay);

        BitmapFont largeFont = new BitmapFont(); // or use a pre-scaled .fnt file
        largeFont.getData().setScale(2f); // make it bigger
        Label.LabelStyle largeTitleStyle = new Label.LabelStyle(largeFont, Color.WHITE);
        Label title = new Label("LEADERBOARD", largeTitleStyle);
        title.setPosition((950 - title.getWidth()) / 2f, 350); // adjust Y as needed


        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(
                new TextureRegion(new Texture(Gdx.files.internal("ui/lbbackground.png"))))
         );
        table.setColor(1, 1, 1, 1); // 75% opacity
        table.center();

        // 3) Position the table in the center of the screen
        table.setSize(400, 300);
        table.setPosition(
                (950  - table.getWidth())  / 2,
                (512 - table.getHeight()) / 2
        );


        // 5) Fetch your stored records (stubbed here)
        String[] records = {
                "Alice    120 sec",
                "Bob      145 sec",
                "Cara     165 sec",
                "Dave     200 sec"
        };

        // 6) One row per record
        for (String rec : records) {
            Label rowLabel = new Label(rec, labelStyle);
            table.add(rowLabel).left().pad(5).row();
        }

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.RED;

        TextButton closeButton = new TextButton("EXIT", buttonStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                leaderboardOverlay.setVisible(false); // Only this toggles visibility now
            }
        });
        table.add(closeButton).padTop(20).row();

        // 7) Add the table to our overlay group
        leaderboardOverlay.addActor(table);
        leaderboardOverlay.addActor(title);
        leaderboardOverlay.setVisible(false);
    }

    private void showLeaderboard() {
        // Toggle its visibility when called
        leaderboardOverlay.setVisible(!leaderboardOverlay.isVisible());
    }
}

