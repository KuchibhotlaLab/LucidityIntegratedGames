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

    WorkingMemoryGame game;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;
    private float elapsed;

    public DifficultyScreen(WorkingMemoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameOneConstants.DIFFICULTY_WORLD_SIZE, GameOneConstants.DIFFICULTY_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
        font.getData().setScale(GameOneConstants.DIFFICULTY_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        viewport.apply();
        if(elapsed < 4) {
            Gdx.gl.glClearColor(1.0f,0.98f,0.78f, 1);
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.setColor(new Color(0.01f, 0.4f, 0.44f, 1));
            font.getData().setScale(GameOneConstants.TITLE_SCALE);
            //TODO: fix the font printing locations here
            font.draw(batch, "Short Term", GameOneConstants.DIFFICULTY_WORLD_SIZE/6, GameOneConstants.DIFFICULTY_WORLD_SIZE *2);
            font.draw(batch, "Memory Test", GameOneConstants.DIFFICULTY_WORLD_SIZE/6, GameOneConstants.DIFFICULTY_WORLD_SIZE);
            batch.end();

        } else {
            Gdx.gl.glClearColor(GameOneConstants.BACKGROUND_COLOR.r, GameOneConstants.BACKGROUND_COLOR.g, GameOneConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(GameOneConstants.EASY_COLOR);
            renderer.circle(GameOneConstants.EASY_CENTER.x, GameOneConstants.EASY_CENTER.y, GameOneConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(GameOneConstants.MEDIUM_COLOR);
            renderer.circle(GameOneConstants.MEDIUM_CENTER.x, GameOneConstants.MEDIUM_CENTER.y, GameOneConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(GameOneConstants.HARD_COLOR);
            renderer.circle(GameOneConstants.HARD_CENTER.x, GameOneConstants.HARD_CENTER.y, GameOneConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(1f);
            font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

            final GlyphLayout easyLayout = new GlyphLayout(font, GameOneConstants.EASY_LABEL);
            font.draw(batch, GameOneConstants.EASY_LABEL, GameOneConstants.EASY_CENTER.x, GameOneConstants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

            final GlyphLayout mediumLayout = new GlyphLayout(font, GameOneConstants.MEDIUM_LABEL);
            font.draw(batch, GameOneConstants.MEDIUM_LABEL, GameOneConstants.MEDIUM_CENTER.x, GameOneConstants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

            final GlyphLayout hardLayout = new GlyphLayout(font, GameOneConstants.HARD_LABEL);
            font.draw(batch, GameOneConstants.HARD_LABEL, GameOneConstants.HARD_CENTER.x, GameOneConstants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

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

        if (worldTouch.dst(GameOneConstants.EASY_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
            game.showMemoryScreen(GameOneConstants.Difficulty.EASY);
        }

        if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
            game.showMemoryScreen(GameOneConstants.Difficulty.MEDIUM);
        }

        if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
            game.showMemoryScreen(GameOneConstants.Difficulty.HARD);
        }

        return true;
    }
}