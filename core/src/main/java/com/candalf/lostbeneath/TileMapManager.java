package com.candalf.lostbeneath;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

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
        map = loader.load("harita/unbenannt.tmx", params);
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
        if (map != null && map.getLayers().get(layerName) != null) {
            return map.getLayers().get(layerName).getObjects();
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