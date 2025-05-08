package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerAnimation {
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> climbAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> hurtAnimation;
    
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean facingRight;
    private PlayerState currentState;
    
    public enum PlayerState {
        IDLE, WALKING, RUNNING, CLIMBING, JUMPING, FALLING, DYING, HURT
    }

    public PlayerAnimation(boolean isKnight) {
        String characterPath = isKnight ? "Knight/" : "Mage/";
        facingRight = true;
        currentState = PlayerState.IDLE;
        stateTime = 0;
        
        loadAnimations(characterPath);
    }

    private void loadAnimations(String characterPath) {
        // Load idle animation (14 frames for Mage, 12 frames for Knight)
        idleAnimation = loadAnimation(characterPath + "Idle/idle", 12, 0.1f, true);
        
        // Load walk animation (6 frames)
        walkAnimation = loadAnimation(characterPath + "Walk/walk", 6, 0.1f, true);
        
        // Load run animation (8 frames)
        runAnimation = loadAnimation(characterPath + "Run/run", 8, 0.08f, true);
        
        // Load climb animation (4 frames)
        climbAnimation = loadAnimation(characterPath + "Climb/climb", 4, 0.1f, true);
        
        // Load jump animation (7 frames)
        jumpAnimation = loadAnimation(characterPath + "Jump/jump", 7, 0.1f, false);
        
        // Load fall animation (use last few frames of jump animation in reverse)
        fallAnimation = loadAnimation(characterPath + "Jump/jump", 3, 0.1f, true);
        
        // Load death animation (10 frames)
        deathAnimation = loadAnimation(characterPath + "Death/death", 10, 0.1f, false);
        
        // Load hurt animation (4 frames)
        hurtAnimation = loadAnimation(characterPath + "Hurt/hurt", 4, 0.1f, false);
        
        // Set initial frame
        currentFrame = idleAnimation.getKeyFrame(0);
    }

    private Animation<TextureRegion> loadAnimation(String basePath, int frameCount, float frameDuration, boolean looping) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            try {
                Texture tex = new Texture(Gdx.files.internal(basePath + i + ".png"));
                frames.add(new TextureRegion(tex));
            } catch (Exception e) {
                Gdx.app.error("PlayerAnimation", "Error loading animation frame: " + basePath + i + ".png");
            }
        }
        return new Animation<>(frameDuration, frames, looping ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
    }

    public void update(float delta, PlayerState newState, boolean isFacingRight) {
        stateTime += delta;
        currentState = newState;
        
        if (this.facingRight != isFacingRight) {
            this.facingRight = isFacingRight;
        }
        
        Animation<TextureRegion> currentAnimation = getAnimationForState(currentState);
        currentFrame = currentAnimation.getKeyFrame(stateTime);
        
        // Flip the frame if needed
        if (currentFrame.isFlipX() != !facingRight) {
            currentFrame.flip(true, false);
        }
    }
    
    private Animation<TextureRegion> getAnimationForState(PlayerState state) {
        switch (state) {
            case IDLE:
                return idleAnimation;
            case WALKING:
                return walkAnimation;
            case RUNNING:
                return runAnimation;
            case CLIMBING:
                return climbAnimation;
            case JUMPING:
                return jumpAnimation;
            case FALLING:
                return fallAnimation;
            case DYING:
                return deathAnimation;
            case HURT:
                return hurtAnimation;
            default:
                return idleAnimation;
        }
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public boolean isAnimationFinished() {
        Animation<TextureRegion> currentAnimation = getAnimationForState(currentState);
        return currentAnimation.isAnimationFinished(stateTime);
    }

    public void resetStateTime() {
        stateTime = 0;
    }

    public void dispose() {
        disposeAnimation(idleAnimation);
        disposeAnimation(walkAnimation);
        disposeAnimation(runAnimation);
        disposeAnimation(climbAnimation);
        disposeAnimation(jumpAnimation);
        disposeAnimation(fallAnimation);
        disposeAnimation(deathAnimation);
        disposeAnimation(hurtAnimation);
    }
    
    private void disposeAnimation(Animation<TextureRegion> animation) {
        if (animation != null) {
            for (Object obj : animation.getKeyFrames()) {
                if (obj instanceof TextureRegion) {
                    TextureRegion region = (TextureRegion)obj;
                    if (region.getTexture() != null) {
                        region.getTexture().dispose();
                    }
                }
            }
        }
    }
}