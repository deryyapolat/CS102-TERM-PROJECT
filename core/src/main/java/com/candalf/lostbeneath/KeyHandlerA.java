package com.candalf.lostbeneath;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class KeyHandlerA extends InputAdapter {
    public boolean moveUp = false;
    public boolean moveDown = false;
    public boolean moveLeft = false;
    public boolean moveRight = false;
    
    @Override
    public boolean keyDown(int keycode) {
        
        switch(keycode) {
            case Keys.W:
                moveUp = true;
                break;
            case Keys.S:
                moveDown = true;
                break;
            case Keys.A:
                moveLeft = true;
                break;
            case Keys.D:
                moveRight = true;
                break;
        }
        return false; // CRITICAL FIX: return false to let the event continue to be processed
    }
    
    @Override
    public boolean keyUp(int keycode) {
        
        switch(keycode) {
            case Keys.W:
                moveUp = false;
                break;
            case Keys.S:
                moveDown = false;
                break;
            case Keys.A:
                moveLeft = false;
                break;
            case Keys.D:
                moveRight = false;
                break;
        }
        return false; // CRITICAL FIX: return false to let the event continue to be processed
    }
}