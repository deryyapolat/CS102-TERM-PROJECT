package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class LevelSelectScreen extends AbstractScreen {
    private Texture mapBg;
    private Texture blankTex;
    private Texture flagUp, flagDown;
    private Texture LevelNotPassedTex;
    static boolean level1Unlocked, level2Unlocked, level3Unlocked, level4Unlocked;
    private Texture levelPassedTex; // Texture for completed levels
    static int level=0;
    private Texture menuTex;
    private ImageButton menuBtn;

    public LevelSelectScreen(Game game, boolean l1, boolean l2, boolean l3, boolean l4) {
        super(game);
        level1Unlocked = true;
        level2Unlocked = l2;
        level3Unlocked = l3;
        level4Unlocked = l4;
    }
    public LevelSelectScreen(Game game) {
        super(game);
        level1Unlocked = true;
        level2Unlocked = false;
        level3Unlocked = false;
        level4Unlocked = false;

        loadLevelProgress();
    }

    @Override
    protected void initialize() {

        mapBg = new Texture(Gdx.files.internal("LevelInformationPage.png"));
        Image bg = new Image(mapBg);
        bg.setFillParent(true);
        stage.addActor(bg);

        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(0,0,0,0);
        pm.fill();
        blankTex = new Texture(pm);
        pm.dispose();
        TextureRegionDrawable blankDr = new TextureRegionDrawable(new TextureRegion(blankTex));

        try {
            LevelNotPassedTex = new Texture(Gdx.files.internal("LevelNotPassed.png"));
            levelPassedTex = new Texture(Gdx.files.internal("LevelPassed.png"));

            Gdx.app.log("LevelSelectScreen", "Level button textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("LevelSelectScreen", "Error loading level textures: " + e.getMessage());

            Pixmap lockedPm = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
            lockedPm.setColor(Color.RED);
            lockedPm.fill();
            
            Pixmap unlockedPm = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
            unlockedPm.setColor(Color.GREEN);
            unlockedPm.fill();
            
            LevelNotPassedTex = new Texture(lockedPm);
            levelPassedTex = new Texture(unlockedPm);
            
            lockedPm.dispose();
            unlockedPm.dispose();
        }
        
        TextureRegionDrawable lockDr = new TextureRegionDrawable(new TextureRegion(LevelNotPassedTex));
        TextureRegionDrawable unlockDr = new TextureRegionDrawable(new TextureRegion(levelPassedTex));

        Vector2[] lockPos = {
                new Vector2(267,400),  // Level 1
                new Vector2(452,266),  // Level 2
                new Vector2(491,75),   // Level 3
                new Vector2(742,313)   // Level 4
        };
        float lockSize = 60f;

        for (int i = 0; i < lockPos.length; i++) {
            final int levelCount = i+1;
            level=levelCount;
            Vector2 p = lockPos[i];

            boolean levelUnlocked;
            switch(levelCount) {
                case 1: levelUnlocked = true; break;
                case 2: levelUnlocked = level2Unlocked; break;
                case 3: levelUnlocked = level3Unlocked; break;
                case 4: levelUnlocked = level4Unlocked; break;
                default: levelUnlocked = false;
            }

            TextureRegionDrawable upDrawable = levelUnlocked ? unlockDr : lockDr;
            TextureRegionDrawable downDrawable = levelUnlocked ? unlockDr : lockDr;

            ImageButton levelBtn = new ImageButton(upDrawable, downDrawable);

            Gdx.app.log("LevelSelectScreen", "Level " + levelCount + " unlocked: " + levelUnlocked);

            Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), levelUnlocked ? Color.GREEN : Color.RED);
            levelBtn.setSize(lockSize, lockSize);
            levelBtn.setPosition(p.x - lockSize/2, p.y - lockSize/2);

            int currentLevel = levelCount;
            boolean isUnlocked = levelUnlocked;
            
            levelBtn.addListener(new ClickListener() {
                @Override 
                public void clicked(InputEvent e, float x, float y) {
                    Gdx.app.log("LevelSelectScreen", "Level button clicked: Level " + currentLevel + ", Unlocked: " + isUnlocked);
                    if (isUnlocked) {
                        game.setScreen(new GamePanel(game, currentLevel));
                    } else {
                        showLockedLevelMessage();
                    }
                }
            });
            stage.addActor(levelBtn);
        }

        flagUp   = new Texture(Gdx.files.internal("unclickedMomentFlag.png"));
        flagDown = new Texture(Gdx.files.internal("clickingMomentFlag.png"));
        TextureRegionDrawable upDr   = new TextureRegionDrawable(new TextureRegion(flagUp));
        TextureRegionDrawable downDr = new TextureRegionDrawable(new TextureRegion(flagDown));

        Vector2[] flagPos = {
                new Vector2(210,220),
                new Vector2(310,160),
                new Vector2(440,430),
                new Vector2(660,140),
                new Vector2(610,400)
        };
        float fw=55f, fh=55f;
        for (int i = 0; i < flagPos.length; i++) {
            final int flagIndex = i;
            Vector2 p = flagPos[i];
            ImageButton f = new ImageButton(upDr, downDr);
            f.setSize(fw, fh);
            f.setPosition(p.x-fw/2, p.y-fh/2);
            f.addListener(new ClickListener(){
                @Override public void clicked(InputEvent e, float x, float y) {
                    Gdx.app.log("LevelSelectScreen", "Flag button clicked: Flag " + flagIndex);
                    showInfoDialog(flagIndex);
                }
            });
            stage.addActor(f);
        }
        menuTex = new Texture(Gdx.files.internal("menu.png"));
        TextureRegionDrawable menuDr = new TextureRegionDrawable(new TextureRegion(menuTex));

        menuBtn = new ImageButton(menuDr);

        final float MARGIN = 3;
        menuBtn.setSize(100, 100);
        menuBtn.setPosition(
                stage.getViewport().getWorldWidth()  - menuBtn.getWidth() + 5,
                stage.getViewport().getWorldHeight() - menuBtn.getHeight() + 30
        );

        menuBtn.addListener(new ClickListener(){
            @Override public boolean touchDown(InputEvent event, float x, float y, int ptr, int button){
                menuBtn.getImage().setColor(Color.DARK_GRAY);
                return super.touchDown(event, x, y, ptr, button);
            }
            @Override public void touchUp(InputEvent event, float x, float y, int ptr, int button){
                menuBtn.getImage().setColor(Color.WHITE);
                super.touchUp(event, x, y, ptr, button);
            }
            @Override public void clicked(InputEvent event, float x, float y){
                // go back to your main menu
                game.setScreen(new MenuScreen((Main)game));
            }
        });

        stage.addActor(menuBtn);
    }

    private void showInfoDialog(int flagIndex) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(0f, 0f, 0f, 0.7f);
        pm.fill();
        final Texture windowBgTex = new Texture(pm);
        pm.dispose();
        TextureRegionDrawable windowBgDr = new TextureRegionDrawable(new TextureRegion(windowBgTex));

        BitmapFont font = new BitmapFont();
        Window.WindowStyle ws = new Window.WindowStyle(font, Color.WHITE, windowBgDr);
        final Dialog dlg = new Dialog("", ws);

        String[] flagMessages = {
                "Orpheus and Eurydice stepped into the unknown, where light could no longer follow.\n" +
                        "The stone sealed behind them, cutting off the world they once knew.\n" +
                        "What lay ahead was not just darkness, but memory twisted into maze.\n" +
                        "Together, they would descend, not to escape, but to understand their fate.\n" +
                        "\n HINT: In this place, fire doesn't guide, it tests. Be careful where you step.",

                "The deeper they went, the more the silence fractured.\n" +
                        "Whispers crawled along the walls, voices that weren’t theirs.\n" +
                        "Eurydice hesitated, Orpheus tightened his grip.\n" +
                        "Even fear had form in these tunnels, and it walked beside them.\n" +
                        "\n HINT: The poison burns, but the spinning blade slices fast. Don't just run—observe, plan, and move when the danger sleeps. ",

                "At the edge of a broken platform, they saw a faint glimmer.\n" +
                        "It was a key, nestled in shadow, breathing like it had life.\n" +
                        "To reach it meant trust in crumbling ground and shifting time.\n" +
                        "Or fall into a chasm where names are forgotten.\n " +
                        "\n HINT: Fire can be your end—or your guide. When the flames block your path, look for the rhythm in their dance ",

                "Each gate opened into something worse. Tunnels flooded, air thick with rot.\n" +
                        "Memories surfaced, a song unfinished, a promise unkept.\n" +
                        "The labyrinth wasn’t just stone, it was woven from their regrets.\n" +
                        "And the further they walked, the heavier it became. \n " +
                        "\n HINT: Use ropes and wall jumps to ascend without falling into the green poison. Don’t rush. Some traps are hidden just outside your view.",

                "Before them, the path split, one trail in light, one in dark.\n" +
                        "No signs, only the sound of their own breath and beating hearts.\n" +
                        "Orpheus looked back once. Eurydice didn’t speak.\n" +
                        "In this place, doubt was deadly, and trust was everything."
        };

        String message = (flagIndex >= 0 && flagIndex < flagMessages.length) ? flagMessages[flagIndex] : "An unknown tale.";

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(message, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.center);
        float width = stage.getViewport().getWorldWidth() * 0.85f;
        label.setWidth(width);

        dlg.getContentTable().add(label).width(width).pad(20).row();

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = font;
        tbs.fontColor = Color.RED;
        TextButton exit = new TextButton("Exit", tbs);
        exit.getLabel().setFontScale(1.2f);

        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                dlg.hide();
                windowBgTex.dispose();
            }
        });

        dlg.getButtonTable().add(exit).pad(10);
        dlg.show(stage);
        dlg.pack();
        dlg.setPosition(
                (stage.getViewport().getWorldWidth()  - dlg.getWidth())  / 2f,
                (stage.getViewport().getWorldHeight() - dlg.getHeight()) / 2f
        );
    }

    private void showLockedLevelMessage() {
        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(0f, 0f, 0f, 0.7f);
        pm.fill();
        Texture bgTex = new Texture(pm);
        pm.dispose();
        TextureRegionDrawable bgDr = new TextureRegionDrawable(new TextureRegion(bgTex));

        BitmapFont font = new BitmapFont();
        Window.WindowStyle ws = new Window.WindowStyle(font, Color.WHITE, bgDr);
        final Dialog dlg = new Dialog("", ws);

        Label msgLabel = new Label("Complete the previous level first!", new Label.LabelStyle(font, Color.RED));
        msgLabel.setFontScale(1.5f);
        dlg.getContentTable().add(msgLabel).pad(20);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = font;
        tbs.fontColor = Color.WHITE;
        TextButton okBtn = new TextButton("OK", tbs);
        
        dlg.getButtonTable().add(okBtn).width(100).height(40).padBottom(10);
        
        okBtn.addListener(new ClickListener() {
            @Override 
            public void clicked(InputEvent e, float x, float y) {
                dlg.hide();
                bgTex.dispose();
            }
        });
        
        dlg.show(stage);
        dlg.pack();
        dlg.setPosition(
            (stage.getViewport().getWorldWidth() - dlg.getWidth()) / 2f,
            (stage.getViewport().getWorldHeight() - dlg.getHeight()) / 2f
        );
    }

    private void loadLevelProgress() {
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("LostBeneath");
        level1Unlocked = true; // Level 1 is always unlocked
        level2Unlocked = prefs.getBoolean("level2Unlocked", false);
        level3Unlocked = prefs.getBoolean("level3Unlocked", false);
        level4Unlocked = prefs.getBoolean("level4Unlocked", false);
    }

    private void saveLevelProgress() {
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("LostBeneath");
        prefs.putBoolean("level2Unlocked", level2Unlocked);
        prefs.putBoolean("level3Unlocked", level3Unlocked);
        prefs.putBoolean("level4Unlocked", level4Unlocked);
        prefs.flush();
    }

    public static void unlockNextLevel(int completedLevel) {
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("LostBeneath");
        
        Gdx.app.log("LevelSelectScreen", "Unlocking next level after completing level " + completedLevel);
        level1Unlocked = true;
        prefs.putBoolean("level1Unlocked", true);

        if (completedLevel >= 1) {
            level1Unlocked = true;
            prefs.putBoolean("level1Unlocked", true);
        }

        switch (completedLevel) {
            case 1:
                level2Unlocked = true;
                prefs.putBoolean("level2Unlocked", true);
                Gdx.app.log("LevelSelectScreen", "Unlocked level 2");
                break;
            case 2:
                level3Unlocked = true;
                prefs.putBoolean("level3Unlocked", true);
                Gdx.app.log("LevelSelectScreen", "Unlocked level 3");
                break;
            case 3:
                level4Unlocked = true;
                prefs.putBoolean("level4Unlocked", true);
                Gdx.app.log("LevelSelectScreen", "Unlocked level 4");
                break;
            case 4:
                Gdx.app.log("LevelSelectScreen", "All levels completed!");
                break;
            default:
                Gdx.app.error("LevelSelectScreen", "Invalid level number: " + completedLevel);
                break;
        }

        prefs.flush();

        Gdx.app.log("LevelSelectScreen", "Updated level unlock status:");
        Gdx.app.log("LevelSelectScreen", "  Level 1: always unlocked");
        Gdx.app.log("LevelSelectScreen", "  Level 2: " + prefs.getBoolean("level2Unlocked", false));
        Gdx.app.log("LevelSelectScreen", "  Level 3: " + prefs.getBoolean("level3Unlocked", false));
        Gdx.app.log("LevelSelectScreen", "  Level 4: " + prefs.getBoolean("level4Unlocked", false));
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            Gdx.app.log("LevelSelectScreen", "Touch detected at: " + x + ", " + y);
        }
    }

    @Override
    public void show() {
        super.show();
        loadLevelProgress();
        Gdx.app.log("LevelSelectScreen", "Level unlocks: " + 
            level1Unlocked + ", " + level2Unlocked + ", " + 
            level3Unlocked + ", " + level4Unlocked);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        mapBg.dispose();
        blankTex.dispose();
        flagUp.dispose();
        flagDown.dispose();
        LevelNotPassedTex.dispose();
        if (levelPassedTex != null) {
            levelPassedTex.dispose();
        }

        saveLevelProgress();
    }

}
