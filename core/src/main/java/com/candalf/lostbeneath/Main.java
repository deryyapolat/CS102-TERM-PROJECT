package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class Main extends Game {
    private AssetManager assetManager;

    @Override
    public void create() {
        // Initialize asset manager
        assetManager = new AssetManager();
        
        // Set asset path
        Gdx.files.local("assets/").file().mkdirs();
        
        // Start with the menu screen
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // This will render the active screen
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
}
