package com.candalf.lostbeneath;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class MultiplayerInputProcessor extends InputAdapter {
    private final KeyHandlerA keyHandlerA;
    private final KeyHandlerB keyHandlerB;

    public MultiplayerInputProcessor(KeyHandlerA keyHandlerA, KeyHandlerB keyHandlerB) {
        this.keyHandlerA = keyHandlerA;
        this.keyHandlerB = keyHandlerB;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Handle Player A input
        switch(keycode) {
            case Keys.W:
                keyHandlerA.moveUp = true;
                return true;
            case Keys.S:
                keyHandlerA.moveDown = true;
                return true;
            case Keys.A:
                keyHandlerA.moveLeft = true;
                return true;
            case Keys.D:
                keyHandlerA.moveRight = true;
                return true;
        }

        // Handle Player B input
        switch(keycode) {
            case Keys.UP:
                keyHandlerB.moveUp = true;
                return true;
            case Keys.DOWN:
                keyHandlerB.moveDown = true;
                return true;
            case Keys.LEFT:
                keyHandlerB.moveLeft = true;
                return true;
            case Keys.RIGHT:
                keyHandlerB.moveRight = true;
                return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Handle Player A input
        switch(keycode) {
            case Keys.W:
                keyHandlerA.moveUp = false;
                return true;
            case Keys.S:
                keyHandlerA.moveDown = false;
                return true;
            case Keys.A:
                keyHandlerA.moveLeft = false;
                return true;
            case Keys.D:
                keyHandlerA.moveRight = false;
                return true;
        }

        // Handle Player B input
        switch(keycode) {
            case Keys.UP:
                keyHandlerB.moveUp = false;
                return true;
            case Keys.DOWN:
                keyHandlerB.moveDown = false;
                return true;
            case Keys.LEFT:
                keyHandlerB.moveLeft = false;
                return true;
            case Keys.RIGHT:
                keyHandlerB.moveRight = false;
                return true;
        }

        return false;
    }
}