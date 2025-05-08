package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD implements Disposable {
    private Stage stage;
    private Viewport viewport;
    private Player playerA;
    private Player playerB;
    
    private Label playerAHealthLabel;
    private Label playerBHealthLabel;
    private Label playerANameLabel;
    private Label playerBNameLabel;
    
    public HUD(SpriteBatch batch, Player playerA, Player playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
        
        viewport = new FitViewport(1920, 1080);
        stage = new Stage(viewport, batch);
        
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        
        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        
        playerANameLabel = new Label("Knight", style);
        playerBNameLabel = new Label("Mage", style);
        playerAHealthLabel = new Label(String.format("HP: %d/%d", playerA.getHealth(), playerA.getMaxHealth()), style);
        playerBHealthLabel = new Label(String.format("HP: %d/%d", playerB.getHealth(), playerB.getMaxHealth()), style);
        
        table.add(playerANameLabel).padTop(10).padLeft(10);
        table.add(playerBNameLabel).padTop(10).padRight(10).expandX();
        table.row();
        table.add(playerAHealthLabel).padLeft(10);
        table.add(playerBHealthLabel).padRight(10).expandX();
        
        stage.addActor(table);
    }
    
    public void update() {
        playerAHealthLabel.setText(String.format("HP: %d/%d", playerA.getHealth(), playerA.getMaxHealth()));
        playerBHealthLabel.setText(String.format("HP: %d/%d", playerB.getHealth(), playerB.getMaxHealth()));
    }
    
    public void render() {
        stage.draw();
    }
    
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}