package com.candalf.lostbeneath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class FirstScreen implements Screen {

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Player player1;
    private Player player2;
    private GameWorld gameWorld;

    // Drawing parameters
    private final float circleRadius = 5f;
    private final float heartGap      = 8f;
    private final float margin        = 10f;

    public FirstScreen(Game game) {
        // Create game world with physics
        gameWorld = new GameWorld();
        
        // Create players with correct parameters
        player1 = new Player(gameWorld.getBox2DWorld(), 100, 100, 5, 2000f, true);  // Knight
        player2 = new Player(gameWorld.getBox2DWorld(), 300, 100, 5, 2000f, false); // Mage
        
        // Set up player controls
        player1.setControls(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);
        player2.setControls(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
        
        // Add players to game world
        gameWorld.addEntity(player1);
        gameWorld.addEntity(player2);
    }

    @Override
    public void show() {
        // Set up the camera with proper viewport scaling
        camera = new OrthographicCamera();
        float viewportHeight = 1024;
        float viewportWidth = 1920;
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.position.set(viewportWidth / 2f, viewportHeight / 2f, 0);
        camera.update();
        
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        
        // CRITICAL FIX: Make sure this screen receives input events
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }
            
            @Override
            public boolean keyUp(int keycode) {
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera to follow players (average position)
        Vector2 p1Pos = player1.getPosition();
        Vector2 p2Pos = player2.getPosition();
        float targetX = (p1Pos.x + p2Pos.x) / 2;
        float targetY = (p1Pos.y + p2Pos.y) / 2;
        
        // Add camera bounds to keep map visible
        float mapWidth = gameWorld.getMapManager().getMapWidth();
        float mapHeight = gameWorld.getMapManager().getMapHeight();
        
        targetX = MathUtils.clamp(targetX, camera.viewportWidth/2, mapWidth - camera.viewportWidth/2);
        targetY = MathUtils.clamp(targetY, camera.viewportHeight/2, mapHeight - camera.viewportHeight/2);
        
        camera.position.set(targetX, targetY, 0);
        camera.update();
        
        // Update game world
        gameWorld.update(delta);
        
        // Draw game world
        batch.setProjectionMatrix(camera.combined);
        gameWorld.render(batch);
        
        // Draw UI elements
        batch.begin();
        drawNames();
        drawHearts();
        batch.end();
    }

    private void drawHearts() {
        // Compute Y positions
        float p1Y = camera.viewportHeight - (circleRadius * 3 + margin);
        float p2Y = p1Y - (circleRadius * 3 + heartGap);

        // Draw Player1 hearts
        String name1 = "Knight";
        layout.setText(font, name1);
        float startX1 = margin + layout.width + heartGap;
        drawHeartsForPlayer(startX1, p1Y, player1, Color.RED);

        // Draw Player2 hearts
        String name2 = "Mage";
        layout.setText(font, name2);
        float startX2 = margin + layout.width + heartGap;
        drawHeartsForPlayer(startX2, p2Y, player2, Color.BLUE);
    }

    private void drawHeartsForPlayer(float startX, float y, Player player, Color fillColor) {
        float x = startX;
        for (int i = 0; i < player.getMaxHealth(); i++) {
            Color c = i < player.getHealth() ? fillColor : Color.DARK_GRAY;
            drawHeartShape(x, y, c);
            x += circleRadius * 4 + heartGap;
        }
    }

    private void drawHeartShape(float x, float y, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x + circleRadius, y + circleRadius * 2, circleRadius);
        shapeRenderer.circle(x + circleRadius * 3, y + circleRadius * 2, circleRadius);
        shapeRenderer.triangle(
            x, y + circleRadius * 2,
            x + circleRadius * 4, y + circleRadius * 2,
            x + circleRadius * 2, y
        );
    }

    private void drawNames() {
        // Compute Y positions matching hearts
        float p1Y = camera.viewportHeight - (circleRadius * 3 + margin);
        float p2Y = p1Y - (circleRadius * 3 + heartGap);

        // Draw names to the left of hearts
        font.setColor(Color.WHITE);
        font.draw(batch, "Knight", margin, p1Y + circleRadius * 2);
        font.draw(batch, "Mage", margin, p2Y + circleRadius * 2);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width / (float)height;
        float viewportHeight = 1024;
        float viewportWidth = viewportHeight * aspectRatio;
        camera.viewportWidth = viewportWidth;
        camera.viewportHeight = viewportHeight;
        camera.update();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        player1.dispose();
        player2.dispose();
        gameWorld.dispose();
    }
}
