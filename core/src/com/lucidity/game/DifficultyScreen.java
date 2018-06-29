package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DifficultyScreen extends InputAdapter implements Screen {
    public static final String TAG = DifficultyScreen.class.getName();

    MemoryGame game;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;
    private float elapsed;

    public DifficultyScreen(MemoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(Constants.DIFFICULTY_WORLD_SIZE, Constants.DIFFICULTY_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont();
        font.getData().setScale(Constants.DIFFICULTY_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        viewport.apply();
        if(elapsed < 2) {
            Gdx.gl.glClearColor(1.0f,0.98f,0.78f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.setColor(new Color(0.01f, 0.4f, 0.44f, 1));
            font.getData().setScale(5f);
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);

            font.draw(batch, "Remember the position of light colored blocks",Constants.DIFFICULTY_WORLD_SIZE/8, Constants.DIFFICULTY_WORLD_SIZE/2);
            batch.end();

        } else {
            Gdx.gl.glClearColor(Constants.BACKGROUND_COLOR.r, Constants.BACKGROUND_COLOR.g, Constants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(Constants.EASY_COLOR);
            renderer.circle(Constants.EASY_CENTER.x, Constants.EASY_CENTER.y, Constants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(Constants.MEDIUM_COLOR);
            renderer.circle(Constants.MEDIUM_CENTER.x, Constants.MEDIUM_CENTER.y, Constants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(Constants.HARD_COLOR);
            renderer.circle(Constants.HARD_CENTER.x, Constants.HARD_CENTER.y, Constants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(1f);

            final GlyphLayout easyLayout = new GlyphLayout(font, Constants.EASY_LABEL);
            font.draw(batch, Constants.EASY_LABEL, Constants.EASY_CENTER.x, Constants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

            final GlyphLayout mediumLayout = new GlyphLayout(font, Constants.MEDIUM_LABEL);
            font.draw(batch, Constants.MEDIUM_LABEL, Constants.MEDIUM_CENTER.x, Constants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

            final GlyphLayout hardLayout = new GlyphLayout(font, Constants.HARD_LABEL);
            font.draw(batch, Constants.HARD_LABEL, Constants.HARD_CENTER.x, Constants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        batch.dispose();
        font.dispose();
        renderer.dispose();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 worldTouch = viewport.unproject(new Vector2(screenX, screenY));

        if (worldTouch.dst(Constants.EASY_CENTER) < Constants.DIFFICULTY_BUBBLE_RADIUS) {
            game.showMemoryScreen(Constants.Difficulty.EASY);
        }

        if (worldTouch.dst(Constants.MEDIUM_CENTER) < Constants.DIFFICULTY_BUBBLE_RADIUS) {
            game.showMemoryScreen(Constants.Difficulty.MEDIUM);
        }

        if (worldTouch.dst(Constants.HARD_CENTER) < Constants.DIFFICULTY_BUBBLE_RADIUS) {
            game.showMemoryScreen(Constants.Difficulty.HARD);
        }

        return true;
    }
}