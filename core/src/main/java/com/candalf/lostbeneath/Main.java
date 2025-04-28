package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        Main game = Main.this;
        setScreen(new MenuScreen(this));
    }
}
