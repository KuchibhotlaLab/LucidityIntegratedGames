package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by lixiaoyan on 7/12/18.
 */

public class ObjectRecognitionDifficultyScreen extends InputAdapter implements Screen {
    ObjectRecognitionGame game;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;
    float elapsed;

    public ObjectRecognitionDifficultyScreen(ObjectRecognitionGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameThreeConstants.MODE_WORLD_SIZE, GameThreeConstants.MODE_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
        font.getData().setScale(GameThreeConstants.MODE_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        Gdx.gl.glClearColor(GameThreeConstants.BACKGROUND_COLOR.r, GameThreeConstants.BACKGROUND_COLOR.g, GameThreeConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(viewport.getCamera().combined);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(GameThreeConstants.EASY_COLOR);
        renderer.circle(GameThreeConstants.EASY_CENTER.x, GameThreeConstants.EASY_CENTER.y, GameThreeConstants.MODE_BUBBLE_RADIUS);

        renderer.setColor(GameThreeConstants.MEDIUM_COLOR);
        renderer.circle(GameThreeConstants.MEDIUM_CENTER.x, GameThreeConstants.MEDIUM_CENTER.y, GameThreeConstants.MODE_BUBBLE_RADIUS);

        renderer.setColor(GameThreeConstants.HARD_COLOR);
        renderer.circle(GameThreeConstants.HARD_CENTER.x, GameThreeConstants.HARD_CENTER.y, GameThreeConstants.MODE_BUBBLE_RADIUS);

        renderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.getData().setScale(GameThreeConstants.MODE_LABEL_SCALE);
        font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

        final GlyphLayout easyLayout = new GlyphLayout(font, GameOneConstants.EASY_LABEL);
        font.draw(batch, GameOneConstants.EASY_LABEL, GameThreeConstants.EASY_CENTER.x, GameThreeConstants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

        final GlyphLayout mediumLayout = new GlyphLayout(font, GameOneConstants.MEDIUM_LABEL);
        font.draw(batch, GameOneConstants.MEDIUM_LABEL, GameThreeConstants.MEDIUM_CENTER.x, GameThreeConstants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

        final GlyphLayout hardLayout = new GlyphLayout(font, GameOneConstants.HARD_LABEL);
        font.draw(batch, GameOneConstants.HARD_LABEL, GameThreeConstants.HARD_CENTER.x, GameThreeConstants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {viewport.update(width, height, true);}

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
            game.setScreen(new ObjectRecognitionScreen(game, GameThreeConstants.DIFFICULTY_EASY));
        }

        if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
            game.setScreen(new ObjectRecognitionScreen(game, GameThreeConstants.DIFFICULTY_MEDIUM));
        }

        if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
            game.setScreen(new ObjectRecognitionScreen(game, GameThreeConstants.DIFFICULTY_HARD));
        }

        return true;
    }
}
