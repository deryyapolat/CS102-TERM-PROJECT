package com.candalf.lostbeneath;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class KeyHandlerB extends InputAdapter {
    public boolean moveUp = false;
    public boolean moveDown = false;
    public boolean moveLeft = false;
    public boolean moveRight = false;

    @Override
    public boolean keyDown(int keycode) {
        
        switch (keycode) {
            case Keys.UP:
                moveUp = true;
                break;
            case Keys.DOWN:
                moveDown = true;
                break;
            case Keys.LEFT:
                moveLeft = true;
                break;
            case Keys.RIGHT:
                moveRight = true;
                break;
        }
        return false; // CRITICAL FIX: return false to let the event continue to be processed
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.UP:
                moveUp = false;
                break;
            case Keys.DOWN:
                moveDown = false;
                break;
            case Keys.LEFT:
                moveLeft = false;
                break;
            case Keys.RIGHT:
                moveRight = false;
                break;
        }
        return false; // CRITICAL FIX: return false to let the event continue to be processed
    }
}