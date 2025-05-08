package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

public class MapManager {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Array<Ice> iceBlocks;
    private Array<Rectangle> climbableAreas;
    private Array<Wall> wallBlocks; // Array to store Wall objects
    private World world;
    
    public MapManager(World world) {
        this.world = world;
        this.iceBlocks = new Array<>();
        this.climbableAreas = new Array<>();
        this.wallBlocks = new Array<>(); // Initialize the wall blocks array
        loadMap();
    }
    
    private void loadMap() {
        try {
            // Load the TMX map with extensive debugging
            map = new TmxMapLoader().load("assets/harita/unbenannt.tmx");
            System.out.println("[MAP] Map loaded successfully");
            
            if (map == null) {
                System.err.println("[ERROR] Map failed to load but no exception was thrown!");
                return;
            }
            
            // List all layers for debugging
            System.out.println("[MAP] Available layers:");
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                System.out.println("- " + map.getLayers().get(i).getName());
            }
            
            renderer = new OrthogonalTiledMapRenderer(map);
            
            // Process ice objects
            loadIceObjects();
            
            // Process rope objects
            loadRopeObjects();
            
            // Load wall objects
            loadWallObjects();
            
            // Process other object layers
            processObjectLayer();
            
        } catch (Exception e) {
            System.err.println("[CRITICAL ERROR] Failed to load map: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadIceObjects() {
        System.out.println("[ICE] Loading ice objects");
        MapObjects iceObjects = map.getLayers().get("ice").getObjects();
        if (iceObjects != null) {
            System.out.println("[ICE] Found " + iceObjects.getCount() + " ice objects");
            int iceCount = 0;
            for (RectangleMapObject obj : iceObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                iceBlocks.add(new Ice(world, rect.x, rect.y, rect.width, rect.height, obj.getProperties()));
                iceCount++;
            }
            System.out.println("[ICE] Created " + iceCount + " ice objects");
        } else {
            System.out.println("[ICE] No ice layer found in map");
        }
    }
    
    private void loadRopeObjects() {
        System.out.println("[ROPE] Loading rope objects");
        MapObjects ropeObjects = map.getLayers().get("rope").getObjects();
        if (ropeObjects != null) {
            System.out.println("[ROPE] Found " + ropeObjects.getCount() + " rope objects");
            int ropeCount = 0;
            for (RectangleMapObject obj : ropeObjects.getByType(RectangleMapObject.class)) {
                Rectangle rect = obj.getRectangle();
                climbableAreas.add(rect);
                ropeCount++;
            }
            System.out.println("[ROPE] Created " + ropeCount + " rope areas");
        } else {
            System.out.println("[ROPE] No rope layer found in map");
        }
    }
    
    private void loadWallObjects() {
        System.out.println("[WALLS] Attempting to load wall objects from object layer 'wallColliders'");
        wallBlocks.clear();
        boolean wallsLoaded = false;
        try {
            // Direct object layer 'wallColliders'
            MapLayer layer = map.getLayers().get("wallColliders");
            if (layer != null && layer.getObjects() != null && layer.getObjects().getCount() > 0) {
                MapObjects objects = layer.getObjects();
                System.out.println("[WALLS] Found " + objects.getCount() + " wall objects in 'wallColliders'");
                int count = 0;
                for (RectangleMapObject obj : objects.getByType(RectangleMapObject.class)) {
                    Rectangle r = obj.getRectangle();
                    Wall w = new Wall(world, r.x, r.y, r.width, r.height, obj.getProperties());
                    wallBlocks.add(w);
                    count++;
                }
                System.out.println("[WALLS] Created " + count + " walls from 'wallColliders'");
                wallsLoaded = true;
            }
        } catch (Exception e) {
            System.err.println("[WALLS] Error loading 'wallColliders': " + e.getMessage());
        }
        if (!wallsLoaded) {
            System.err.println("[WALLS] No wall objects found in layer 'wallColliders'");
        }
    }
    
    private void processObjectLayer() {
        MapLayer objectLayer = map.getLayers().get("Objects");
        if (objectLayer != null) {
            MapObjects objects = objectLayer.getObjects();
            System.out.println("[OBJECTS] Processing general Objects layer with " + objects.getCount() + " objects");
            
            // Process each object
            for (MapObject object : objects) {
                MapProperties props = object.getProperties();
                
                try {
                    // Get object properties
                    float x = props.get("x", Float.class);
                    float y = props.get("y", Float.class);
                    float width = props.get("width", Float.class);
                    float height = props.get("height", Float.class);
                    
                    String type = props.get("type", String.class);
                    if (type != null) {
                        if (type.equals("ice")) {
                            Ice ice = new Ice(world, x, y, width, height, props);
                            iceBlocks.add(ice);
                            System.out.println("[OBJECTS] Created ice: " + x + "," + y);
                        } else if (type.equals("rope") || type.equals("ladder")) {
                            climbableAreas.add(new Rectangle(x, y, width, height));
                            System.out.println("[OBJECTS] Created climbable area: " + x + "," + y);
                        } else if (type.equals("Wall") || type.equals("wall")) {
                            Wall wall = new Wall(world, x, y, width, height, props);
                            wallBlocks.add(wall);
                            System.out.println("[OBJECTS] Created wall from Objects layer: " + x + "," + y);
                        } else {
                            System.out.println("[OBJECTS] Unknown object type: " + type);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to process object: " + e.getMessage());
                }
            }
        } else {
            System.out.println("[OBJECTS] No 'Objects' layer found in map");
        }
    }
    
    public void update(float delta, Player player1, Player player2) {
        // Update ice blocks
        for (Ice ice : iceBlocks) {
            ice.update(delta);
        }
        
        // Update wall blocks
        for (Wall wall : wallBlocks) {
            wall.update(delta);
        }
        
        // Check for climbing areas
        boolean player1Climbing = false;
        boolean player2Climbing = false;
        Rectangle p1Bounds = new Rectangle(player1.getX(), player1.getY(), 64, 64);
        Rectangle p2Bounds = new Rectangle(player2.getX(), player2.getY(), 64, 64);
        
        for (Rectangle climbArea : climbableAreas) {
            if (climbArea.overlaps(p1Bounds)) {
                player1Climbing = true;
            }
            if (climbArea.overlaps(p2Bounds)) {
                player2Climbing = true;
            }
        }
        
        player1.setClimbing(player1Climbing);
        player2.setClimbing(player2Climbing);
        
        // Check ice collisions
        checkIceCollisions(player1);
        checkIceCollisions(player2);
        
        // Check wall collisions
        checkWallCollisions(player1);
        checkWallCollisions(player2);
    }
    
    public void render() {
        if (renderer != null) {
            renderer.render();
        }
    }
    
    public void checkIceCollisions(Player player) {
        Rectangle playerBounds = new Rectangle(
            player.getX() - 32, 
            player.getY() - 32,
            64, 
            64
        );
        
        for (Ice ice : iceBlocks) {
            Rectangle iceBounds = new Rectangle(
                ice.getX() - ice.getWidth()/2,
                ice.getY() - ice.getHeight()/2,
                ice.getWidth(),
                ice.getHeight()
            );
            
            if (playerBounds.overlaps(iceBounds)) {
                ice.applyIceEffect(player);
            }
        }
    }
    
    public void checkWallCollisions(Player player) {
        Rectangle playerBounds = new Rectangle(
            player.getX() - 32, 
            player.getY() - 32,
            64, 
            64
        );
        
        for (Wall wall : wallBlocks) {
            Rectangle wallBounds = new Rectangle(
                wall.getX() - wall.getWidth()/2,
                wall.getY() - wall.getHeight()/2,
                wall.getWidth(),
                wall.getHeight()
            );
            
            if (playerBounds.overlaps(wallBounds)) {
                // Apply wall effect like ice effect
                wall.applyWallEffect(player);
                System.out.println("Player on wall: " + player.getPosition());
            }
        }
    }
    
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
        for (Ice ice : iceBlocks) {
            ice.dispose();
        }
    }
    
    public void setView(com.badlogic.gdx.graphics.OrthographicCamera camera) {
        if (renderer != null) {
            renderer.setView(camera);
        }
    }
    
    public TiledMap getMap() {
        return map;
    }
}