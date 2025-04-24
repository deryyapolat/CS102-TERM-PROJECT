package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstScreen implements Screen {

    private Main game;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Player player1;
    private Player player2;

    // Drawing parameters
    private final float circleRadius = 5f;
    private final float heartGap      = 8f;
    private final float margin        = 10f;

    public FirstScreen(Main game) {
        this.game = game;
        player1 = new Player(100, 100, Color.RED,   5, 200f);
        player2 = new Player(300, 100, Color.BLUE,  5, 200f);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Update players
        player1.update(delta, Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);
        player2.update(delta, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F))     player1.damage();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) player2.damage();

        // Draw hearts and players
        shapeRenderer.begin(ShapeType.Filled);
        player1.render(shapeRenderer);
        player2.render(shapeRenderer);
        drawHearts();
        shapeRenderer.end();

        // Draw names next to hearts
        batch.begin();
        drawNames();
        batch.end();
    }

    private void drawHearts() {
        // Compute Y positions
        float p1Y = camera.viewportHeight - (circleRadius * 3 + margin);
        float p2Y = p1Y - (circleRadius * 3 + heartGap);

        // Draw Player1 hearts after measuring name width
        String name1 = "Eurydice";
        layout.setText(font, name1);
        float startX1 = margin + layout.width + heartGap;
        drawHeartsForPlayer(startX1, p1Y, player1, Color.RED);

        // Draw Player2 hearts
        String name2 = "Orpheus";
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

        // Draw left of hearts
        font.setColor(Color.WHITE);
        font.draw(batch, "Eurydice", margin, p1Y + circleRadius * 2);
        font.draw(batch, "Orpheus", margin, p2Y + circleRadius * 2);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
