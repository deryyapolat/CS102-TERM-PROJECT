package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import java.util.Iterator;
import java.util.Iterator;

public class TileMapManager {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private final float unitScale = 1f;
    
    public TileMapManager() {
        // Load the Tiled map from the mapler folder
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.generateMipMaps = true;
        
        TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
        // Load the Tiled map from the harita folder under the assets directory
        System.out.println("[DEBUG] Loading TMX map from harita/unbenannt.tmx");
        map = loader.load("harita/unbenannt.tmx", params);
        
        if (map != null) {
            System.out.println("[DEBUG] Map loaded successfully. Inspecting layers:");
            if (map.getLayers() != null) {
                int layerCount = map.getLayers().getCount();
                System.out.println("[DEBUG] Map has " + layerCount + " layers:");
                for (int i = 0; i < layerCount; i++) {
                    String layerName = map.getLayers().get(i).getName();
                    String layerType = map.getLayers().get(i).getClass().getSimpleName();
                    System.out.println("[DEBUG]   - Layer #" + i + ": '" + layerName + 
                                     "' (Type: " + layerType + ")");
                    
                    // If this is an object layer, print how many objects it has
                    if (map.getLayers().get(i).getObjects() != null) {
                        int objCount = map.getLayers().get(i).getObjects().getCount();
                        System.out.println("[DEBUG]     Has " + objCount + " objects");
                    }
                }
            } else {
                System.out.println("[DEBUG] Map has no layers array");
            }
        } else {
            System.err.println("[ERROR] Failed to load TMX map!");
        }
        
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);
    }

    public void render(OrthographicCamera camera) {
        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }
    }
    
    public float getMapWidth() {
        if (map == null) return 0;
        int mapWidth = map.getProperties().get("width", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        return mapWidth * tileWidth * unitScale;
    }
    
    public float getMapHeight() {
        if (map == null) return 0;
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        return mapHeight * tileHeight * unitScale;
    }

    public MapObjects getLayerObjects(String layerName) {
        if (map != null) {
            if (map.getLayers().get(layerName) != null) {
                MapObjects objects = map.getLayers().get(layerName).getObjects();
                // Debug output to understand the layer and its objects
                System.out.println("[DEBUG] Layer '" + layerName + "' found with " + 
                                 (objects != null ? objects.getCount() : "null") + " objects");
                
                // If this is a health-related layer, let's print detailed info about each object
                if ((layerName.equals("heal") || layerName.equals("healObj")) && objects != null) {
                    System.out.println("[DEBUG] Detailed health objects inspection in layer '" + layerName + "':");
                    int objCount = 0;
                    for (MapObject obj : objects) {
                        objCount++;
                        System.out.println("  - Object #" + objCount + 
                                         ", Type: " + obj.getProperties().get("type", String.class));
                        
                        // Print all properties of the object
                        System.out.println("    Properties:");
                        Iterator<String> keys = obj.getProperties().getKeys();
                        while (keys.hasNext()) {
                            String propName = keys.next();
                            System.out.println("      " + propName + ": " + obj.getProperties().get(propName));
                        }
                        
                        if (obj instanceof RectangleMapObject) {
                            Rectangle r = ((RectangleMapObject)obj).getRectangle();
                            System.out.println("    Position: (" + r.x + ", " + r.y + 
                                             "), Size: " + r.width + "x" + r.height);
                        }
                    }
                }
                
                return objects;
            } else {
                System.out.println("[DEBUG] Layer '" + layerName + "' not found in the map");
            }
        } else {
            System.out.println("[DEBUG] Map is null when trying to get layer: " + layerName);
        }
        return null;
    }
    
    public Rectangle[] getCollisionBounds(String layerName) {
        MapObjects objects = getLayerObjects(layerName);
        if (objects == null) return new Rectangle[0];
        
        Rectangle[] bounds = new Rectangle[objects.getCount()];
        int i = 0;
        for (RectangleMapObject obj : objects.getByType(RectangleMapObject.class)) {
            bounds[i++] = obj.getRectangle();
        }
        return bounds;
    }
    
    public TiledMap getMap() {
        return map;
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }
    
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
    }
}