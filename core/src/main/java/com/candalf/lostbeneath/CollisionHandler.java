package com.candalf.lostbeneath;

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
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Pre-collision logic can be implemented here if needed
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
                
                // Set or unset wall contact status
                String sensorId = (String) playerFixture.getUserData();
                
                // For foot sensors touching walls, set canJump (wall is treated as ground)
                if (FOOT_SENSOR_ID.equals(sensorId)) {
                    player.setCanJump(isBeginning);
                } 
                // For side sensors, enable wall sliding/jumping
                else if (LEFT_SENSOR_ID.equals(sensorId) || RIGHT_SENSOR_ID.equals(sensorId)) {
                    player.setOnWall(isBeginning);
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
}