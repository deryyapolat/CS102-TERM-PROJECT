package com.candalf.lostbeneath;

import java.util.Arrays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.List;

public class GamePanel implements Screen {
    private static final float WORLD_WIDTH = 1920;
    private static final float WORLD_HEIGHT = 1024;
    
    private final Game game;
    private GameWorld gameWorld;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private HUD hud;
    
    // Use regular Player instances
    private Player playerA; // Knight
    private Player playerB; // Mage
    private KeyHandlerA keyHandlerA;
    private KeyHandlerB keyHandlerB;
    private InputMultiplexer inputMultiplexer;
    private Animation<TextureRegion> knightAnimation;
    private Animation<TextureRegion> mageAnimation;
    
    // Game over functionality
    private boolean gameOver = false;
    private float gameOverTimer = 0;
    private static final float RESTART_DELAY = 3.0f; // 3 seconds before restart
    private BitmapFont gameOverFont;
    private GlyphLayout gameOverLayout;
    private ShapeRenderer shapeRenderer;

    public GamePanel(Game game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.batch = new SpriteBatch();
        
        // Initialize input handlers
        this.keyHandlerA = new KeyHandlerA();
        this.keyHandlerB = new KeyHandlerB();
        this.inputMultiplexer = new InputMultiplexer(keyHandlerA, keyHandlerB);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Initialize game world
        this.gameWorld = new GameWorld();
        
        
        // Create players at spawn points
        MapObjects spawnPoint1 = gameWorld.getMapManager().getLayerObjects("spawningpoint1");
        MapObjects spawnPoint2 = gameWorld.getMapManager().getLayerObjects("spawningpoint2");
        
        float x1 = 100, y1 = 100; // Default positions
        float x2 = 500, y2 = 500;
        
        if (spawnPoint1 != null && spawnPoint1.getCount() > 0) {
            Rectangle spawn1 = ((RectangleMapObject)spawnPoint1.get(0)).getRectangle();
            x1 = spawn1.x;
            y1 = spawn1.y;
        }
        
        if (spawnPoint2 != null && spawnPoint2.getCount() > 0) {
            Rectangle spawn2 = ((RectangleMapObject)spawnPoint2.get(0)).getRectangle();
            x2 = spawn2.x;
            y2 = spawn2.y;
        }
        
        // Create regular player instances instead of specialized classes
        this.playerA = new Player(gameWorld.getBox2DWorld(), x1, y1, 5, 200f, true);  // Knight
        this.playerB = new Player(gameWorld.getBox2DWorld(), x2, y2, 5, 200f, false); // Mage
        
        gameWorld.addEntity(playerA);
        gameWorld.addEntity(playerB);
        

        
        
        // Initialize HUD
        this.hud = new HUD(batch, playerA, playerB);
        
        // Center camera initially
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        
        // Initialize game over resources
        gameOverFont = new BitmapFont();
        gameOverFont.getData().setScale(4.0f); // Make the font larger
        gameOverLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        
        // Load animations
        initializeAnimations();
    }
    
    private void initializeAnimations() {
        // Load knight animation
        Texture knightTexture = new Texture(Gdx.files.internal("Knight/knight.png"));
        TextureRegion[][] knightFrames = TextureRegion.split(knightTexture, 64, 64);
        knightAnimation = new Animation<>(0.1f, knightFrames[0]);
        
        // Load mage animation
        Texture mageTexture = new Texture(Gdx.files.internal("Mage/mage.png"));
        TextureRegion[][] mageFrames = TextureRegion.split(mageTexture, 64, 64);
        mageAnimation = new Animation<>(0.1f, mageFrames[0]);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Force both players to be active bodies (not "asleep")
        playerA.getBody().setAwake(true);
        playerB.getBody().setAwake(true);
        
        // Apply movement directly based on key handlers with stronger forces
        float moveSpeed = 200.0f; // Adjusted for velocity-based movement
        
        // *** KEY CHANGE: Apply gravity differently to avoid interference with horizontal movement ***
        
        // Player A (Knight) movement
        boolean knightMoved = false;
        if (keyHandlerA.moveLeft) {
            if (playerA.canJump) {
                // On ground - can fully control movement
                playerA.getBody().setLinearVelocity(-moveSpeed, playerA.getBody().getLinearVelocity().y);
            } else {
                // In air - use direct velocity setting but preserve Y velocity
                // This gives more responsive air control while still allowing gravity to work
                float currentYVel = playerA.getBody().getLinearVelocity().y;
                playerA.getBody().setLinearVelocity(-moveSpeed * 0.7f, currentYVel);
            }
            playerA.setFacingRight(false); // Knight facing left
            knightMoved = true;
        } 
        else if (keyHandlerA.moveRight) {
            if (playerA.canJump) {
                // On ground - can fully control movement
                playerA.getBody().setLinearVelocity(moveSpeed, playerA.getBody().getLinearVelocity().y);
            } else {
                // In air - use direct velocity setting but preserve Y velocity
                float currentYVel = playerA.getBody().getLinearVelocity().y;
                playerA.getBody().setLinearVelocity(moveSpeed * 0.7f, currentYVel);
            }
            playerA.setFacingRight(true); // Knight facing right
            knightMoved = true;
        }
        else {
            // Stop horizontal movement when no keys pressed
            if (playerA.canJump) {
                // Only stop horizontal movement on ground
                playerA.getBody().setLinearVelocity(0, playerA.getBody().getLinearVelocity().y);
            } else {
                // In air, apply slight damping instead of stopping completely
                float currentXVel = playerA.getBody().getLinearVelocity().x;
                float currentYVel = playerA.getBody().getLinearVelocity().y;
                playerA.getBody().setLinearVelocity(currentXVel * 0.98f, currentYVel);
            }
        }
        
        // Apply extra gravity when in air regardless of movement
        if (!playerA.canJump) {
            // Apply consistent downward force for reliable gravity
            playerA.getBody().applyForceToCenter(0, -3000f, true);
        }
        
        // Knight jump with increased force to match stronger gravity
        if (keyHandlerA.moveUp && playerA.canJump) {
            // Apply jump force using the constant from Player class with appropriate multiplier
            playerA.getBody().setLinearVelocity(playerA.getBody().getLinearVelocity().x, Player.JUMP_FORCE * 8);
            playerA.setCanJump(false);
            playerA.setAnimationState(PlayerAnimation.PlayerState.JUMPING);
        }
        
        // Knight wall jump with increased force
        if (keyHandlerA.moveUp && !playerA.canJump && playerA.isOnWall()) {
            float jumpDirectionX = playerA.getBody().getLinearVelocity().x < 0 ? 
                Player.WALL_JUMP_FORCE_X * 4 : -Player.WALL_JUMP_FORCE_X * 4;
            
            playerA.getBody().setLinearVelocity(jumpDirectionX, Player.WALL_JUMP_FORCE_Y * 6);
            playerA.setAnimationState(PlayerAnimation.PlayerState.JUMPING);
            // Set facing direction opposite to wall
            playerA.setFacingRight(jumpDirectionX > 0);
        }
        
        // Knight animation priorities (improved to handle mid-air movement better)
        // Only update animation state if the player isn't dying
        if (playerA.getHealth() > 0) {
            if (playerA.isJumping()) {
                if (playerA.getBody().getLinearVelocity().y > 0) {
                    playerA.setAnimationState(PlayerAnimation.PlayerState.JUMPING);
                } else {
                    playerA.setAnimationState(PlayerAnimation.PlayerState.FALLING);
                }
            } else if (knightMoved) {
                playerA.setAnimationState(PlayerAnimation.PlayerState.WALKING);
            } else {
                playerA.setAnimationState(PlayerAnimation.PlayerState.IDLE);
            }
        }
        
        // Apply the same physics approach for Player B (Mage)
        boolean mageMoved = false;
        
        // Mage left movement
        if (keyHandlerB.moveLeft) {
            if (playerB.canJump) {
                // On ground - can fully control movement
                playerB.getBody().setLinearVelocity(-moveSpeed, playerB.getBody().getLinearVelocity().y);
            } else {
                // In air - use direct velocity setting but preserve Y velocity
                float currentYVel = playerB.getBody().getLinearVelocity().y;
                playerB.getBody().setLinearVelocity(-moveSpeed * 0.7f, currentYVel);
            }
            playerB.setFacingRight(false); // Mage facing left
            mageMoved = true;
        } 
        // Mage right movement
        else if (keyHandlerB.moveRight) {
            if (playerB.canJump) {
                // On ground - can fully control movement
                playerB.getBody().setLinearVelocity(moveSpeed, playerB.getBody().getLinearVelocity().y);
            } else {
                // In air - use direct velocity setting but preserve Y velocity
                float currentYVel = playerB.getBody().getLinearVelocity().y;
                playerB.getBody().setLinearVelocity(moveSpeed * 0.7f, currentYVel);
            }
            playerB.setFacingRight(true); // Mage facing right
            mageMoved = true;
        }
        else {
            // Stop horizontal movement when no keys pressed
            if (playerB.canJump) {
                // Only stop horizontal movement on ground
                playerB.getBody().setLinearVelocity(0, playerB.getBody().getLinearVelocity().y);
            } else {
                // In air, apply slight damping instead of stopping completely
                float currentXVel = playerB.getBody().getLinearVelocity().x;
                float currentYVel = playerB.getBody().getLinearVelocity().y;
                playerB.getBody().setLinearVelocity(currentXVel * 0.98f, currentYVel);
            }
        }
        
        // Apply extra gravity when in air regardless of movement
        if (!playerB.canJump) {
            // Apply consistent downward force for reliable gravity
            playerB.getBody().applyForceToCenter(0, -3000f, true);
        }
        
        // Mage jump with increased force to match stronger gravity
        if (keyHandlerB.moveUp && playerB.canJump) {
            playerB.getBody().setLinearVelocity(playerB.getBody().getLinearVelocity().x, Player.JUMP_FORCE * 8);
            playerB.setCanJump(false);
            playerB.setAnimationState(PlayerAnimation.PlayerState.JUMPING);
        }
        
        // Mage wall jump with increased force
        if (keyHandlerB.moveUp && !playerB.canJump && playerB.isOnWall()) {
            float jumpDirectionX = playerB.getBody().getLinearVelocity().x < 0 ? 
                Player.WALL_JUMP_FORCE_X * 4 : -Player.WALL_JUMP_FORCE_X * 4;
            
            playerB.getBody().setLinearVelocity(jumpDirectionX, Player.WALL_JUMP_FORCE_Y * 6);
            playerB.setAnimationState(PlayerAnimation.PlayerState.JUMPING);
            playerB.setFacingRight(jumpDirectionX > 0);
        }
        
        // Mage animation priorities (enhanced to handle mid-air movement better)
        // Only update animation state if the player isn't dying
        if (playerB.getHealth() > 0) {
            if (playerB.isJumping()) {
                if (playerB.getBody().getLinearVelocity().y > 0) {
                    playerB.setAnimationState(PlayerAnimation.PlayerState.JUMPING);
                } else {
                    playerB.setAnimationState(PlayerAnimation.PlayerState.FALLING);
                }
            } else if (mageMoved) {
                playerB.setAnimationState(PlayerAnimation.PlayerState.WALKING);
            } else {
                playerB.setAnimationState(PlayerAnimation.PlayerState.IDLE);
            }
        }
        
        // Update game logic (only once per frame)
        gameWorld.update(delta);
        
        // Update camera to follow midpoint between players
        float cameraX = (playerA.getPosition().x + playerB.getPosition().x) / 2;
        float cameraY = (playerA.getPosition().y + playerB.getPosition().y) / 2;
        
        // Add camera bounds to keep it within the map
        cameraX = Math.max(viewport.getWorldWidth() / 2, Math.min(cameraX, WORLD_WIDTH - viewport.getWorldWidth() / 2));
        cameraY = Math.max(viewport.getWorldHeight() / 2, Math.min(cameraY, WORLD_HEIGHT - viewport.getWorldHeight() / 2));
        
        camera.position.set(cameraX, cameraY, 0);
        camera.update();
        
        // Render tilemap with proper camera
        gameWorld.getMapManager().render(camera);
        
        // Render sprites
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        playerA.render(batch);
        playerB.render(batch);
        batch.end();
        
        // Update and render HUD
        hud.update();
        hud.render();
        
        // Check if either player has died
        if ((playerA.getHealth() <= 0 || playerB.getHealth() <= 0) && !gameOver) {
            gameOver = true;
            gameOverTimer = 0;
        }
        
        // Handle game over state
        if (gameOver) {
            // Increment the timer
            gameOverTimer += delta;
            
            // Show "You have failed!" message with screen-space coordinates for the camera view
            
            // Create a semi-transparent black background for the message
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f); // Semi-transparent black
            shapeRenderer.rect(camera.position.x - viewport.getWorldWidth()/2, 
                              camera.position.y - 50, 
                              viewport.getWorldWidth(), 
                              100);
            shapeRenderer.end();
            
            // Draw the text
            batch.begin();
            gameOverLayout.setText(gameOverFont, "You have failed!");
            float messageX = camera.position.x - gameOverLayout.width / 2;
            float messageY = camera.position.y + gameOverLayout.height / 2; // Center vertically
            
            // Add pulsing effect to the text
            float pulse = 0.7f + 0.3f * (float)Math.sin(gameOverTimer * 5);
            gameOverFont.setColor(1, pulse * 0.3f, pulse * 0.3f, 1); // Pulsing red color
            gameOverFont.draw(batch, "You have failed!", messageX, messageY);
            
            // Add "Restarting..." text if timer is over halfway
            if (gameOverTimer > RESTART_DELAY / 2) {
                gameOverLayout.setText(gameOverFont, "Restarting...");
                gameOverFont.setColor(1, 1, 1, 0.5f + 0.5f * (gameOverTimer - RESTART_DELAY/2) / (RESTART_DELAY/2));
                gameOverFont.draw(batch, "Restarting...", 
                                 camera.position.x - gameOverLayout.width / 2, 
                                 messageY - gameOverLayout.height * 1.5f);
            }
            
            batch.end();
            
            // Restart the game after delay
            if (gameOverTimer > RESTART_DELAY) {
                // Reset the game by creating a new GamePanel
                game.setScreen(new GamePanel(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
        // Game is paused
    }

    @Override
    public void resume() {
        // Game is resumed
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameWorld.dispose();
        playerA.dispose();
        playerB.dispose();
        hud.dispose();
        gameOverFont.dispose();
        shapeRenderer.dispose();
    }
}