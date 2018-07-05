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
 * Created by lixiaoyan on 7/3/18.
 */

public class ModeScreen extends InputAdapter implements Screen {

    private FacialMemoryGame game;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;

    public ModeScreen(FacialMemoryGame game) {
        this.game = game;
    }
    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameTwoConstants.MODE_WORLD_SIZE, GameTwoConstants.MODE_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
        font.getData().setScale(GameTwoConstants.MODE_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(viewport.getCamera().combined);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(GameTwoConstants.F2W_COLOR);
        renderer.circle(GameTwoConstants.F2W_CENTER.x, GameTwoConstants.F2W_CENTER.y, GameTwoConstants.MODE_BUBBLE_RADIUS);

        renderer.setColor(GameTwoConstants.W2F_COLOR);
        renderer.circle(GameTwoConstants.W2F_CENTER.x, GameTwoConstants.W2F_CENTER.y, GameTwoConstants.MODE_BUBBLE_RADIUS);
        renderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.getData().setScale(1f);
        font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

        final GlyphLayout easyLayout = new GlyphLayout(font, GameOneConstants.EASY_LABEL);
        font.draw(batch, GameOneConstants.EASY_LABEL, GameTwoConstants.F2W_CENTER.x, GameTwoConstants.F2W_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

        final GlyphLayout mediumLayout = new GlyphLayout(font, GameOneConstants.MEDIUM_LABEL);
        font.draw(batch, GameOneConstants.MEDIUM_LABEL, GameTwoConstants.W2F_CENTER.x, GameTwoConstants.W2F_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

        batch.end();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true);}

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

        if (worldTouch.dst(GameTwoConstants.F2W_CENTER) < GameTwoConstants.MODE_BUBBLE_RADIUS) {
            game.setScreen(new FaceRecogScreen(game, 0, 1));
        }


        return true;
    }
}
