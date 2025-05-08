package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Entity {
    private int health;
    private final int maxHealth;
    protected boolean isClimbing;
    protected boolean canJump;
    protected boolean onWall;
    protected boolean onIce;
    private boolean facingRight;
    private Vector2 previousPosition;
    private Vector2 force; // Added force vector to fix the error
    protected float speed;
    private PlayerAnimation animation;
    private PlayerAnimation.PlayerState currentState;
    
    // Add a new field to track player type
    private final boolean isKnight;
    
    private static final int FRAME_WIDTH = 128;
    private static final int FRAME_HEIGHT = 128;
    private static final float RUN_THRESHOLD = 40f;
    public static final float JUMP_FORCE = 60f; // Increased to match the higher gravity
    public static final float WALL_JUMP_FORCE_X = 30f; // Increased to match higher gravity
    public static final float WALL_JUMP_FORCE_Y = 54f; // Increased to match higher gravity
    private static final float WALL_SLIDE_SPEED = -5f; // Increased wall slide speed for faster sliding
    private static final float DAMPING = 0.8f;
    private static final float AIR_CONTROL = 0.7f; // New constant for air control
    
    protected int upKey, downKey, leftKey, rightKey;
    private boolean keyPressed = false;

    public Player(World world, float startX, float startY, int maxHealth, float speed, boolean isKnight) {
        super(world, startX, startY, FRAME_WIDTH, FRAME_HEIGHT); // Fixed constructor call to match Entity class
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = speed;
        this.isClimbing = false;
        this.canJump = false;
        this.onWall = false;
        this.onIce = false;
        this.facingRight = true;
        this.previousPosition = new Vector2(startX, startY);
        this.force = new Vector2(); // Initialize force vector
        this.currentState = PlayerAnimation.PlayerState.IDLE;
        this.isKnight = isKnight;
        
        // Initialize Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX + FRAME_WIDTH/2, startY + FRAME_HEIGHT/2);
        bodyDef.fixedRotation = true;  // Prevent rotation
        bodyDef.linearDamping = 0f;  // No damping so walls will hold the player in place
        
        body = world.createBody(bodyDef);
        body.setUserData(this); // Set user data for collision detection
        
        // Create main hitbox for collision detection
        PolygonShape mainShape = new PolygonShape();
        mainShape.setAsBox(FRAME_WIDTH/3f, FRAME_HEIGHT/2.5f);
        
        FixtureDef mainFixture = new FixtureDef();
        mainFixture.shape = mainShape;
        mainFixture.density = 1.0f;
        mainFixture.friction = 0.4f;
        mainFixture.restitution = 0.0f; // No bouncing
        
        body.createFixture(mainFixture);
        mainShape.dispose();
        
        // Create foot sensor for ground detection
        PolygonShape footSensor = new PolygonShape();
        footSensor.setAsBox(FRAME_WIDTH/3f, 4f, new Vector2(0, -FRAME_HEIGHT/2.5f), 0);
        
        FixtureDef footFixture = new FixtureDef();
        footFixture.shape = footSensor;
        footFixture.isSensor = true;
        footFixture.density = 0;
        
        Fixture footFix = body.createFixture(footFixture);
        footFix.setUserData("foot_sensor");
        footSensor.dispose();
        
        // Create left wall sensor
        PolygonShape leftSensor = new PolygonShape();
        leftSensor.setAsBox(4f, FRAME_HEIGHT/3f, new Vector2(-FRAME_WIDTH/3f, 0), 0);
        
        FixtureDef leftFixture = new FixtureDef();
        leftFixture.shape = leftSensor;
        leftFixture.isSensor = true;
        
        Fixture leftFix = body.createFixture(leftFixture);
        leftFix.setUserData("left_sensor");
        leftSensor.dispose();
        
        // Create right wall sensor
        PolygonShape rightSensor = new PolygonShape();
        rightSensor.setAsBox(4f, FRAME_HEIGHT/3f, new Vector2(FRAME_WIDTH/3f, 0), 0);
        
        FixtureDef rightFixture = new FixtureDef();
        rightFixture.shape = rightSensor;
        rightFixture.isSensor = true;
        
        Fixture rightFix = body.createFixture(rightFixture);
        rightFix.setUserData("right_sensor");
        rightSensor.dispose();
        
        // Initialize animation system
        this.animation = new PlayerAnimation(isKnight);
        
        // Debug output
    }

    public void setControls(int upKey, int downKey, int leftKey, int rightKey) {
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        
        // Print confirmation to debug
    }

    @Override
    public void update(float delta) {
        // Store current position
        previousPosition.set(body.getPosition());
        
        // Update position from physics body
        position.set(body.getPosition().x - width/2, body.getPosition().y - height/2);
        bounds.setPosition(position.x, position.y);

       
        
        // Only update animation based on current state and direction
        if (animation != null) {
            String playerType = isKnight ? "Knight" : "Mage";
            animation.update(delta, currentState, facingRight);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getCurrentFrame();
        batch.draw(currentFrame, 
                  position.x, position.y,
                  width, height);
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
        
        // Debug
    }

    public void setOnWall(boolean onWall) {
        this.onWall = onWall;
    
    }
    
    public boolean isOnWall() {
        return onWall;
    }
    
    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
    }

    public void damage(int amount) {
        if (currentState != PlayerAnimation.PlayerState.HURT && currentState != PlayerAnimation.PlayerState.DYING) {
            health = Math.max(0, health - amount);
            if (health > 0) {
                currentState = PlayerAnimation.PlayerState.HURT;
                animation.resetStateTime();
            }
        }
    }

    public void setClimbing(boolean climbing) {
        this.isClimbing = climbing;
        if (climbing) {
            currentState = PlayerAnimation.PlayerState.CLIMBING;
            animation.resetStateTime();
        }
    }

    public boolean isClimbing() {
        return isClimbing;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void addForce(Vector2 forceToAdd) {
        // Now we have a force vector that we can add to
        if (force == null) {
            force = new Vector2();
        }
        force.add(forceToAdd);
        
        // Apply this force to the body
        body.applyForceToCenter(force, true);
        
        // Reset force after applying
        force.set(0, 0);
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public void setOnIce(boolean onIce) {
        this.onIce = onIce;
    }
    
    public boolean isOnIce() {
        return onIce;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (animation != null) {
            animation.dispose();
        }
    }
    
    /**
     * Set the facing direction for animation
     */
    public void setFacingRight(boolean isFacingRight) {
        this.facingRight = isFacingRight;
    }
    
    /**
     * Set animation state directly
     */
    public void setAnimationState(PlayerAnimation.PlayerState state) {
        if (this.currentState != state) {
            this.currentState = state;
            // Reset animation timer when state changes
            if (animation != null) {
                animation.resetStateTime();
            }
        }
    }
    
    /**
     * Check if player is currently jumping or falling
     */
    public boolean isJumping() {
        Vector2 vel = body.getLinearVelocity();
        return !canJump || vel.y > 0.5f || vel.y < -0.5f;
    }
    
    /**
     * Check if this player is a knight
     */
    public boolean isKnight() {
        return isKnight;
    }
}
