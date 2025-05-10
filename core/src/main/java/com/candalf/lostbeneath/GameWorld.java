package com.candalf.lostbeneath;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class GameWorld {
    private TileMapManager mapManager;
    private Array<Entity> entities;
    private Array<GameObject> gameObjects;
    private Array<GameObject> objectsToRemove; // List to track objects that need to be removed
    private World box2dWorld;
    private float accumulator = 0;
    private static final float TIME_STEP = 1/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float GRAVITY = -120f;   // Extremely increased gravity for much faster fall
    private Array<Rectangle> buttonZones;
    boolean portalActivated= false;

    public GameWorld() {
        box2dWorld = new World(new Vector2(0, GRAVITY), true);
        entities = new Array<>();
        gameObjects = new Array<>();
        objectsToRemove = new Array<>();
        mapManager = new TileMapManager();
        
        // Add collision handling
        box2dWorld.setContactListener(new CollisionHandler());
        
        loadMapObjects();
        // Prepare button hit zones and initial portal layers
        buttonZones = new Array<>();
        loadButtonZones();
        mapManager.getMap().getLayers().get("closedportal").setVisible(true);
        mapManager.getMap().getLayers().get("openportal").setVisible(false);
    }
    
    private void loadMapObjects() {
        
        // Load walls from TMX file (object layer renamed to "wallColliders")
        MapObjects wallObjects = mapManager.getLayerObjects("wallColliders");
        
        if (wallObjects != null) {
            
            for (RectangleMapObject obj : wallObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                // Create Wall with MapProperties, matching Ice creation pattern
                Wall wall = new Wall(box2dWorld, rect.x, rect.y, rect.width, rect.height, obj.getProperties());
                gameObjects.add(wall);

            }
        } else {
            System.err.println("WARNING: No 'walls' layer found in TMX map!");
            
        }
        
        // Load other objects if needed
        loadIceObjects();
        loadRopeObjects();
        loadPoisonObjects(); // Add this line to load poison/acid objects
        loadHealthObjects(); // Add this line to load health pickup objects
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
    
    private void loadPoisonObjects() {
        MapObjects poisonousObject = mapManager.getLayerObjects("acid");
        if(poisonousObject != null)
        {
            for(RectangleMapObject obj: poisonousObject.getByType(RectangleMapObject.class))
            {
                Rectangle rect = obj.getRectangle();
                // Pass the object properties to the Poison constructor
                gameObjects.add(new Poison(box2dWorld, rect.x, rect.y, rect.width, rect.height, obj.getProperties()));
            }
        }
        // Get the acid layer from TMX file
        com.badlogic.gdx.maps.tiled.TiledMapTileLayer acidLayer = 
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) mapManager.getMap().getLayers().get("acid");
            
        if (acidLayer != null) {
            
            int tileWidth = acidLayer.getTileWidth();
            int tileHeight = acidLayer.getTileHeight();
            
            for (int x = 0; x < acidLayer.getWidth(); x++) {
                for (int y = 0; y < acidLayer.getHeight(); y++) {
                    com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = acidLayer.getCell(x, y);
                    
                    // Check if cell exists and has a tile
                    if (cell != null && cell.getTile() != null) {
                        // Create acid object at this position
                        float worldX = x * tileWidth;
                        float worldY = y * tileHeight;
                        
                        // Get custom properties from the tile if available
                        com.badlogic.gdx.maps.MapProperties tileProps = cell.getTile().getProperties();
                        com.badlogic.gdx.maps.MapProperties props = new com.badlogic.gdx.maps.MapProperties();
                        
                        // Set default properties
                        props.put("damage", 1);  // 1 damage point per interval
                        props.put("interval", 1.0f);  // 1 second interval
                        
                        // Override with tile properties if available
                        Object damage = tileProps.get("damage");
                        if (damage != null) props.put("damage", damage);
                        
                        Object interval = tileProps.get("interval");
                        if (interval != null) props.put("interval", interval);
                        
                        // Add optional visual properties
                        Object color = tileProps.get("color");
                        if (color != null) props.put("color", color);
                        
                        Object alpha = tileProps.get("alpha");
                        if (alpha != null) props.put("alpha", alpha);
                        
                        // Create the poison object with properties
                        Poison poison = new Poison(box2dWorld, worldX, worldY, tileWidth, tileHeight, props);
                        gameObjects.add(poison);
                    }
                }
            }
        } else {
            System.err.println("WARNING: No 'acid' layer found in TMX map!");
        }
    }
    
    private void loadHealthObjects() {
        // Load health objects specifically from the "healObj" layer as found in the TMX
        MapObjects healthObjects = mapManager.getLayerObjects("healObj");
        
        if (healthObjects != null && healthObjects.getCount() > 0) {
            for (RectangleMapObject obj : healthObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                
                // Check if heal amount property exists
                int healAmount = 1; // Default heal amount
                if (obj.getProperties().containsKey("heal")) {
                    try {
                        healAmount = obj.getProperties().get("heal", Integer.class);
                    } catch (Exception e) {
                        String healStr = obj.getProperties().get("heal", String.class);
                        try {
                            healAmount = Integer.parseInt(healStr);
                        } catch (Exception e2) {
                            System.err.println("Error parsing heal amount: " + e2.getMessage());
                        }
                    }
                }
                
                // Create the health pickup and add it to game objects
                Heal healthPickup = new Heal(box2dWorld, rect.x, rect.y, rect.width, rect.height, healAmount);
                gameObjects.add(healthPickup);
            }
        } else {
            System.err.println("No health objects found in layer 'healObj'");
        } 
    }
    
    /**
     * Load interactive BUTTON zones from TMX OBJECT layer 'BUTTON'
     */
    private void loadButtonZones() {
        MapObjects objs = mapManager.getLayerObjects("BUTTON");
        if (objs != null) {
            for (com.badlogic.gdx.maps.MapObject mo : objs) {
                Rectangle zone;
                if (mo instanceof RectangleMapObject) {
                    zone = ((RectangleMapObject)mo).getRectangle();
                } else if (mo instanceof com.badlogic.gdx.maps.objects.PolygonMapObject) {
                    zone = ((com.badlogic.gdx.maps.objects.PolygonMapObject)mo)
                        .getPolygon().getBoundingRectangle();
                } else {
                    continue; // skip unsupported object types
                }
                buttonZones.add(zone);
            }
        }
    }
    
    public void update(float delta) {
        // Portal toggle before physics
        if (!portalActivated) {
            for (Entity e : entities) {
                if (e instanceof Player) {
                    Rectangle pr = e.getBounds();
                    for (Rectangle bz : buttonZones) {
                        if (pr.overlaps(bz)) {
                            mapManager.getMap().getLayers().get("closedportal").setVisible(false);
                            mapManager.getMap().getLayers().get("openportal").setVisible(true);
                            portalActivated = true;
                            break;
                        }
                    }
                }
                if (portalActivated) break;
            }
        }
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
                
                // Track if player is in acid this frame
                boolean inPoisonThisFrame = false;

                // Check for collisions with specific game objects first to detect poison
                for (GameObject obj : gameObjects) {
                    if (obj instanceof Poison) {
                        Poison poison = (Poison)obj;
                        if (entity.getBounds().overlaps(poison.getBounds())) {
                            // Set poisoned state for visual effect and animation
                            player.setPoisoned(true);
                            inPoisonThisFrame = true; // Mark that we're in poison this frame
                            
                            // Apply poison damage if the interval has passed
                            if (poison.canDealDamage()) {
                                player.damage(poison.getDamage());
                                // The damage method will handle initial hurt animation
                            }
                            break; // Found poison collision, can exit this loop early
                        }
                    }
                }

                // Update entity
                entity.update(delta);
                
                // If player wasn't in contact with any poison this frame, reset poisoned state
                if (!inPoisonThisFrame && player.isPoisoned()) {
                    player.setPoisoned(false);
                }
                
                // Check for collisions with other game objects
                for (GameObject obj : gameObjects) {
                    if (obj instanceof Ice && entity.getBounds().overlaps(((Ice)obj).getBounds())) {
                        ((Ice)obj).applyIceEffect(player);
                    }
                    else if (obj instanceof Wall && entity.getBounds().overlaps(((Wall)obj).getBounds())) {
                        // Simply call applyWallEffect, mirroring Ice implementation exactly
                        ((Wall)obj).applyWallEffect(player);
                        player.setOnWall(true);
                    }
                    else if (obj instanceof Rope && entity.getBounds().overlaps(((Rope)obj).getBounds())) {
                        player.setClimbing(true);
                    }
                    else if (obj instanceof Heal) {
                        Heal healthPickup = (Heal)obj;
                        
                        // Skip if already collected
                        if (healthPickup.isCollected()) {
                            continue;
                        }
                        
                        // Improved collision detection with more specific bounds checking
                        Rectangle playerBounds = entity.getBounds();
                        Rectangle healBounds = healthPickup.getBounds();
                        
                        // Use a much larger detection area for more reliable pickup
                        Rectangle expandedPlayerBounds = new Rectangle(
                            playerBounds.x - 30,  // Increased from 25 to 30
                            playerBounds.y - 30,  // Increased from 25 to 30
                            playerBounds.width + 60,  // Increased from 50 to 60
                            playerBounds.height + 60   // Increased from 50 to 60
                        );
                        
                        // Special handling for hurt players - make detection even easier when hurt
                        boolean playerIsHurt = (player.getCurrentState() == PlayerAnimation.PlayerState.HURT || 
                                              player.getCurrentState() == PlayerAnimation.PlayerState.DYING);
                        Rectangle extraLargePlayerBounds = null;
                        
                        if (playerIsHurt) {
                            // When player is hurt or dying, use an extra large detection area
                            extraLargePlayerBounds = new Rectangle(
                                playerBounds.x - 70,  // Increased from 50 to 70
                                playerBounds.y - 70,  // Increased from 50 to 70
                                playerBounds.width + 140, // Increased from 100 to 140
                                playerBounds.height + 140 // Increased from 100 to 140
                            );
                        }
                        
                        // TEST ONLY: Temporarily reduce player health when key is pressed to test healing
                        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.H)) {
                            player.damage(1);
                        }
                        
                        // CRITICAL FIX: Always detect collision when player is hurt and needs healing
                        boolean playerNeedsHealing = player.getHealth() < player.getMaxHealth();
                        
                        // Standard collision detection
                        boolean standardCollision = expandedPlayerBounds.overlaps(healBounds);
                        
                        // Extra large collision detection for hurt players
                        boolean extraLargeCollision = playerIsHurt && extraLargePlayerBounds != null && 
                                                      extraLargePlayerBounds.overlaps(healBounds);
                                                      
                        // Always collect if player is very close and needs healing
                        boolean forceCollect = playerNeedsHealing && 
                                             Vector2.dst(player.getBody().getPosition().x, player.getBody().getPosition().y,
                                                      healBounds.x + healBounds.width/2, healBounds.y + healBounds.height/2) < 150;
                        
                        // Add debug key to test pickups by drawing players to them
                        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.T)) {
                            // Calculate direction to health pickup
                            Vector2 playerPos = player.getBody().getPosition();
                            Vector2 pickupPos = new Vector2(
                                healBounds.x + healBounds.width/2, 
                                healBounds.y + healBounds.height/2
                            );
                            Vector2 direction = new Vector2(
                                pickupPos.x - playerPos.x,
                                pickupPos.y - playerPos.y
                            ).nor().scl(200); // Apply strong force toward pickup
                            
                            // Apply force to move player toward pickup
                            player.getBody().applyForceToCenter(direction, true);
                            
                            // Force player health to be below max to test healing
                            if (player.getHealth() == player.getMaxHealth()) {
                                player.damage(1);
                            }
                        }
                        
                        // We should only activate health pickup if player needs healing OR is actively hurt
                        // This prevents "wasting" pickups when at full health
                        boolean shouldActivatePickup = playerNeedsHealing || playerIsHurt;
                        boolean collisionDetected = (standardCollision || extraLargeCollision || forceCollect) && shouldActivatePickup;
                        
                        if (collisionDetected) {
                            // CRITICAL FIX: Ensure player can be healed if hurt, even at max health
                            if ((player.getCurrentState() == PlayerAnimation.PlayerState.HURT || 
                                 player.getCurrentState() == PlayerAnimation.PlayerState.DYING) && 
                                player.getHealth() == player.getMaxHealth()) {
                                // Temporarily reduce health to ensure healing works correctly
                                player.damage(1);
                            }
                            
                            // FORCE collection regardless of prior conditions
                            healthPickup.collect(player);
                            
                            // Remove the corresponding graphic tile from the TMX 'heal' layer
                            try {
                                com.badlogic.gdx.maps.tiled.TiledMap tmap = mapManager.getMap();
                                com.badlogic.gdx.maps.MapLayer mlayer = tmap.getLayers().get("heal");
                                if (mlayer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                                    com.badlogic.gdx.maps.tiled.TiledMapTileLayer layer = (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) mlayer;
                                    float xWorld = healthPickup.getBounds().x + healthPickup.getBounds().width/2f;
                                    float yWorld = healthPickup.getBounds().y + healthPickup.getBounds().height/2f;
                                    int cellX = (int)(xWorld / layer.getTileWidth());
                                    int cellY = (int)(yWorld / layer.getTileHeight());
                                    layer.setCell(cellX, cellY, null);
                                    // Hide the entire 'heal' layer now that this pickup was collected
                                    layer.setVisible(false);
                                    System.out.println("Cleared tile at (" + cellX + "," + cellY + ") from 'heal' layer");
                                }
                            } catch (Exception e) {
                                System.err.println("Error clearing heal tile: " + e.getMessage());
                            }
                            
                            // Call the force destroy method to guarantee the pickup is destroyed
                            healthPickup.forceDestroy();
                            
                            // IMMEDIATELY mark for removal to ensure it's gone
                            objectsToRemove.add(healthPickup);
                            
                            // Break from the loop after collecting to avoid processing further objects
                            break;
                        }
                    }
                }
            } else {
                // Non-player entities
                entity.update(delta);
            }
        }
        
        // First collect objects that need removal
        objectsToRemove.clear(); // Ensure it's empty before we start
        
        for (GameObject obj : gameObjects) {
            // Check if this is a health pickup that's been collected
            if (obj instanceof Heal && ((Heal) obj).isCollected()) {
                objectsToRemove.add(obj);
            } else {
                // Only update objects that aren't being removed
                obj.update(delta);
            }
        }
        
        // IMMEDIATELY remove collected health pickups from the game objects array
        if (objectsToRemove.size > 0) {
            gameObjects.removeAll(objectsToRemove, true);
            System.out.println("REMOVED " + objectsToRemove.size + " HEALTH PICKUPS"); // Debug message
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
                if (!fixture.isSensor()) {
                    Object userData = fixture.getBody().getUserData();
                    // Skip if this is the player's own body
                    if (userData instanceof Player) {
                        return 1; // Continue to look for other fixtures
                    }
                    
                    // Make sure we only detect ground when the normal is pointing up
                    if (normal.y > 0.7f) { // Normal points mostly upward (close to 1)
                        hitGround[0] = true;
                        return 0; // Stop the raycast
                    }
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