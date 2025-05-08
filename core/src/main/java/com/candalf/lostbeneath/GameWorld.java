package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.candalf.lostbeneath.Wall;

public class GameWorld {
    private TileMapManager mapManager;
    private Array<Entity> entities;
    private Array<GameObject> gameObjects;
    private World box2dWorld;
    private float accumulator = 0;
    private static final float TIME_STEP = 1/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float GRAVITY = -120f;   // Extremely increased gravity for much faster fall

    public GameWorld() {
        box2dWorld = new World(new Vector2(0, GRAVITY), true);
        entities = new Array<>();
        gameObjects = new Array<>();
        mapManager = new TileMapManager();
        
        // Add collision handling
        box2dWorld.setContactListener(new CollisionHandler());
        
        loadMapObjects();
    }
    
    private void loadMapObjects() {
        System.out.println("Loading map objects...");
        
        // Load walls from TMX file (object layer renamed to "wallColliders")
        MapObjects wallObjects = mapManager.getLayerObjects("wallColliders");
        int wallCount = 0;
        
        if (wallObjects != null) {
            wallCount = wallObjects.getCount();
            System.out.println("Found " + wallCount + " wall objects in TMX file");
            
            for (RectangleMapObject obj : wallObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                // Create Wall with MapProperties, matching Ice creation pattern
                Wall wall = new Wall(box2dWorld, rect.x, rect.y, rect.width, rect.height, obj.getProperties());
                gameObjects.add(wall);
                System.out.println("Created Wall at: " + rect.x + "," + rect.y);
            }
        } else {
            System.err.println("WARNING: No 'walls' layer found in TMX map!");
            
        }
        
        // Load other objects if needed
        loadIceObjects();
        loadRopeObjects();
    }
    
    private void loadIceObjects() {
        MapObjects iceObjects = mapManager.getLayerObjects("ice");
        if (iceObjects != null) {
            for (RectangleMapObject obj : iceObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                gameObjects.add(new Ice(box2dWorld, rect.x, rect.y, rect.width, rect.height, obj.getProperties()));
            }
        }
    }
    
    private void loadRopeObjects() {
        MapObjects ropeObjects = mapManager.getLayerObjects("rope");
        if (ropeObjects != null) {
            for (RectangleMapObject obj : ropeObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                gameObjects.add(new Rope(box2dWorld, rect.x, rect.y, rect.width, rect.height));
            }
        }
    }
    
    public void update(float delta) {
        // Fixed time step physics simulation
        accumulator += delta;
        while (accumulator >= TIME_STEP) {
            box2dWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
        
        // For each player entity, check if standing on a wall/platform
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                // Reset wall flag at start of update
                player.setOnWall(false);
                
                // Direct check for ground contact
                boolean onGround = checkPlayerOnGround(player);
                if (onGround) {
                    player.setCanJump(true);
                    
                    // Stop any vertical movement when on ground
                    Vector2 vel = player.getBody().getLinearVelocity();
                    player.getBody().setLinearVelocity(vel.x, 0);
                }

                // Process entity update
                entity.update(delta);
                
                // Check for collisions with game objects (ice, rope, etc)
                for (GameObject obj : gameObjects) {
                    if (obj instanceof Ice && entity.getBounds().overlaps(((Ice)obj).getBounds())) {
                        ((Ice)obj).applyIceEffect(player);
                    }
                    else if (obj instanceof Wall && entity.getBounds().overlaps(((Wall)obj).getBounds())) {
                        // Simply call applyWallEffect, mirroring Ice implementation exactly
                        ((Wall)obj).applyWallEffect(player);
                        player.setOnWall(true);
                        System.out.println("Player on wall: " + player.isKnight() + " at " + player.getPosition().x + "," + player.getPosition().y);
                    }
                    else if (obj instanceof Rope && entity.getBounds().overlaps(((Rope)obj).getBounds())) {
                        player.setClimbing(true);
                    }
                }
            } else {
                // Non-player entities
                entity.update(delta);
            }
        }
        
        // Update any game objects
        for (GameObject obj : gameObjects) {
            obj.update(delta);
        }
    }
    
    /**
     * Checks if a player is standing on the ground or on top of a wall
     */
    private boolean checkPlayerOnGround(Player player) {
        // Get the position and dimensions for checking
        Vector2 position = player.getBody().getPosition();
        Rectangle bounds = player.getBounds();
        float footY = position.y - bounds.height/2.5f;  // Position of the player's feet
        float leftX = position.x - bounds.width/3f;     // Left side of player
        float rightX = position.x + bounds.width/3f;    // Right side of player
        
        // Cast a short ray downward from player's feet
        float rayLength = 5.0f; // Increased distance to check for ground
        
        // Use a single hit flag for all raycasts
        final boolean[] hitGround = {false};
        
        // Create a single callback for our raycasts
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // Accept any non-sensor fixture as a ground
                // This matches how Ice works - it doesn't set any userData
                if (!fixture.isSensor()) {
                    Object userData = fixture.getBody().getUserData();
                    // Skip if this is the player's own body
                    if (userData instanceof Player) {
                        return 1; // Continue to look for other fixtures
                    }
                    hitGround[0] = true;
                    return 0; // Stop the raycast
                }
                return 1; // Continue the raycast
            }
        };
        
        // Cast rays at different points along the player's width
        // Left foot
        box2dWorld.rayCast(callback, new Vector2(leftX, footY), new Vector2(leftX, footY - rayLength));
        
        // Center foot (only check if not hit yet)
        if (!hitGround[0]) {
            box2dWorld.rayCast(callback, new Vector2(position.x, footY), new Vector2(position.x, footY - rayLength));
        }
        
        // Right foot (only check if not hit yet)
        if (!hitGround[0]) {
            box2dWorld.rayCast(callback, new Vector2(rightX, footY), new Vector2(rightX, footY - rayLength));
        }
        
        // Direct collision test as fallback using AABB query
        if (!hitGround[0]) {
            // Define a small rectangle below the player's feet
            final float queryWidth = bounds.width/3;
            final float queryHeight = 5.0f; // Increased height for better detection
            final float queryY = footY - 2.0f;
            
            // Query for any fixtures in this area
            box2dWorld.QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(Fixture fixture) {
                    // Accept any non-sensor fixture as a ground
                    if (!fixture.isSensor()) {
                        Object userData = fixture.getBody().getUserData();
                        // Skip if this is the player's own body
                        if (userData instanceof Player) {
                            return true; // Continue checking
                        }
                        hitGround[0] = true;
                        return false; // Stop the query
                    }
                    return true; // Continue checking
                }
            }, position.x - queryWidth, queryY - queryHeight, position.x + queryWidth, queryY);
        }
        
        return hitGround[0];
    }
    
    public void render(SpriteBatch batch) {
        // Create camera for tile map rendering
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1024); // Match map dimensions
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        
        batch.setProjectionMatrix(camera.combined);
        mapManager.render(camera);
        
        batch.begin();
        // Draw game objects
        for (GameObject obj : gameObjects) {
            if (obj instanceof Ice) {
                continue; // Ice tiles are rendered by the tile map
            }
            obj.render(batch);
        }
        
        // Draw entities
        for (Entity entity : entities) {
            entity.render(batch);
        }
        batch.end();
    }
    
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addGameObject(GameObject object) {
        gameObjects.add(object);
    }
    
    public World getBox2DWorld() {
        return box2dWorld;
    }
    
    public TileMapManager getMapManager() {
        return mapManager;
    }
    
    public void dispose() {
        for (GameObject obj : gameObjects) {
            obj.dispose();
        }
        for (Entity entity : entities) {
            entity.dispose();
        }
        mapManager.dispose();
        box2dWorld.dispose();
    }
}