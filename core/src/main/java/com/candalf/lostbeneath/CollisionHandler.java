package com.candalf.lostbeneath;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Handles collision detection between game entities
 */
public class CollisionHandler implements ContactListener {
    
    // Constants for user data identification
    private static final String WALL_ID = "wall";
    private static final String GROUND_ID = "ground";
    private static final String ICE_ID = "ice";
    private static final String POISON_ID = "poison"; // New constant for poison/acid
    private static final String FOOT_SENSOR_ID = "foot_sensor";
    private static final String LEFT_SENSOR_ID = "left_sensor";
    private static final String RIGHT_SENSOR_ID = "right_sensor";

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        // Process player-wall collision (treat walls as both walls and ground)
        handlePlayerWallCollision(fixtureA, fixtureB, true);
        handlePlayerWallCollision(fixtureB, fixtureA, true);
        
        // Process player-ground collision (this will handle actual ground if it exists)
        handlePlayerGroundCollision(fixtureA, fixtureB, true);
        handlePlayerGroundCollision(fixtureB, fixtureA, true);
        
        // Process player-ice collision
        handlePlayerIceCollision(fixtureA, fixtureB, true);
        handlePlayerIceCollision(fixtureB, fixtureA, true);
        
        // Process player-poison collision
        handlePlayerPoisonCollision(fixtureA, fixtureB, true);
        handlePlayerPoisonCollision(fixtureB, fixtureA, true);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        // Process player-wall collision end
        handlePlayerWallCollision(fixtureA, fixtureB, false);
        handlePlayerWallCollision(fixtureB, fixtureA, false);
        
        // Process player-ground collision end
        handlePlayerGroundCollision(fixtureA, fixtureB, false);
        handlePlayerGroundCollision(fixtureB, fixtureA, false);
        
        // Process player-ice collision end
        handlePlayerIceCollision(fixtureA, fixtureB, false);
        handlePlayerIceCollision(fixtureB, fixtureA, false);
        
        // Process player-poison collision end
        handlePlayerPoisonCollision(fixtureA, fixtureB, false);
        handlePlayerPoisonCollision(fixtureB, fixtureA, false);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        // Check if this is a player-wall collision
        boolean playerWallCollision = 
            (fixtureA.getBody().getUserData() instanceof Player && fixtureB.getBody().getUserData() instanceof Wall) ||
            (fixtureB.getBody().getUserData() instanceof Player && fixtureA.getBody().getUserData() instanceof Wall);
            
        if (playerWallCollision) {
            // Determine which fixture is the player and which is the wall
            Fixture playerFixture = (fixtureA.getBody().getUserData() instanceof Player) ? fixtureA : fixtureB;
            Fixture wallFixture = (fixtureA.getBody().getUserData() instanceof Wall) ? fixtureA : fixtureB;
            
            // Get player and wall
            Player player = (Player)playerFixture.getBody().getUserData();
            Wall wall = (Wall)wallFixture.getBody().getUserData();
            
            // Check if player is above the wall
            Vector2 playerPos = player.getBody().getPosition();
            Rectangle playerBounds = player.getBounds();
            Rectangle wallBounds = wall.getBounds();
            
            float playerBottom = playerPos.y - playerBounds.height/2;
            float wallTop = wallBounds.y + wallBounds.height;
            
            // Define a small threshold to determine if player is "on top" of the wall
            float topThreshold = 10f;
            
            // If player's bottom is near or above the wall's top, disable side collisions
            if (playerBottom > wallTop - topThreshold) {
                // When the player is above the wall, we'll disable the collision completely
                // This allows the player to move freely on top of the wall without side collisions
                contact.setEnabled(false);
                
                // Also manually verify if they're standing on top using rays in the game update loop
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Post-collision logic can be implemented here if needed
    }
    
    /**
     * Checks for collision between player sensor and wall
     */
    private void handlePlayerWallCollision(Fixture playerFixture, Fixture otherFixture, boolean isBeginning) {
        // Check if we have a player-wall collision
        if (isPlayerSensor(playerFixture) && isWall(otherFixture)) {
            Object userData = playerFixture.getBody().getUserData();
            if (userData instanceof Player) {
                Player player = (Player) userData;
                String sensorId = (String) playerFixture.getUserData();
                
                // Get player and wall positions to check if player is above the wall
                Vector2 playerPos = player.getBody().getPosition();
                Rectangle playerBounds = player.getBounds();
                
                // Get wall's Body from the fixture
                Body wallBody = otherFixture.getBody();
                GameObject wallObject = (GameObject) wallBody.getUserData();
                float wallTop = 0;
                
                if (wallObject instanceof Wall) {
                    Wall wall = (Wall) wallObject;
                    Rectangle wallBounds = wall.getBounds();
                    wallTop = wallBounds.y + wallBounds.height;
                }
                
                // Calculate player bottom position
                float playerBottom = playerPos.y - playerBounds.height/2;
                
                // For foot sensors touching walls, only treat them as ground if they're on top of the wall
                if (FOOT_SENSOR_ID.equals(sensorId)) {
                    // Check if the player's bottom is very close to the wall's top (standing on it)
                    float topThreshold = 5f; // Small tolerance for detection
                    float playerFeet = playerPos.y - playerBounds.height/2;
                    
                    // If player is standing on top of wall (feet near wall top) OR the player is below the wall
                    if (Math.abs(playerFeet - wallTop) < topThreshold || playerFeet < wallTop) {
                        player.setCanJump(isBeginning);
                    }
                } 
                // For side sensors, enable wall sliding/jumping ONLY if player is not standing on top of the wall
                else if ((LEFT_SENSOR_ID.equals(sensorId) || RIGHT_SENSOR_ID.equals(sensorId))) {
                    // Only apply wall contact if the player's bottom is at least 10 units below the wall's top
                    float threshold = 10f; // Tolerance to prevent wall contact when player is near the top
                    if (playerBottom < wallTop - threshold) {
                        player.setOnWall(isBeginning);
                    }
                }
            }
        }
    }
    
    /**
     * Checks for collision between player foot sensor and ground
     */
    private void handlePlayerGroundCollision(Fixture playerFixture, Fixture otherFixture, boolean isBeginning) {
        if (isPlayerFootSensor(playerFixture) && isGround(otherFixture)) {
            Object userData = playerFixture.getBody().getUserData();
            if (userData instanceof Player) {
                Player player = (Player) userData;
                player.setCanJump(isBeginning);
            }
        }
    }
    
    /**
     * Checks for collision between player and ice surfaces
     */
    private void handlePlayerIceCollision(Fixture playerFixture, Fixture otherFixture, boolean isBeginning) {
        if (isPlayerFootSensor(playerFixture) && isIce(otherFixture)) {
            Object userData = playerFixture.getBody().getUserData();
            if (userData instanceof Player) {
                Player player = (Player) userData;
                player.setOnIce(isBeginning);
            }
        }
    }
    
    /**
     * Checks for collision between player and poison surfaces
     */
    private void handlePlayerPoisonCollision(Fixture playerFixture, Fixture otherFixture, boolean isBeginning) {
        // Check if we have a player-poison collision
        if (playerFixture.getBody().getUserData() instanceof Player && isPoison(otherFixture)) {
            // Collision detection only - damage will be applied in GameWorld update
            // This ensures consistent damage timing based on the poison object's damage interval
        }
    }
    
    /**
     * Checks if a fixture is a player foot sensor
     */
    private boolean isPlayerFootSensor(Fixture fixture) {
        return fixture.getUserData() != null && FOOT_SENSOR_ID.equals(fixture.getUserData());
    }
    
    /**
     * Checks if a fixture is a player wall sensor (left or right)
     */
    private boolean isPlayerSensor(Fixture fixture) {
        return fixture.getUserData() != null && 
               (FOOT_SENSOR_ID.equals(fixture.getUserData()) || 
                LEFT_SENSOR_ID.equals(fixture.getUserData()) || 
                RIGHT_SENSOR_ID.equals(fixture.getUserData()));
    }
    
    /**
     * Checks if a fixture is a wall
     */
    private boolean isWall(Fixture fixture) {
        return fixture.getUserData() != null && WALL_ID.equals(fixture.getUserData());
    }
    
    /**
     * Checks if a fixture is ground
     */
    private boolean isGround(Fixture fixture) {
        return fixture.getUserData() != null && GROUND_ID.equals(fixture.getUserData());
    }
    
    /**
     * Checks if a fixture is ice
     */
    private boolean isIce(Fixture fixture) {
        return fixture.getUserData() != null && ICE_ID.equals(fixture.getUserData());
    }
    
    /**
     * Checks if a fixture is poison
     */
    private boolean isPoison(Fixture fixture) {
        return fixture.getUserData() != null && POISON_ID.equals(fixture.getUserData()) &&
               fixture.getBody().getUserData() instanceof Poison;
    }
}