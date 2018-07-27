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
    float elapsed;

    public ModeScreen(FacialMemoryGame game) {
        this.game = game;
    }
    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameTwoConstants.MODE_WORLD_SIZE, GameTwoConstants.MODE_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(GameTwoConstants.MODE_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        viewport.apply();
        Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(viewport.getCamera().combined);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(GameTwoConstants.F2W_COLOR);
        renderer.circle(GameTwoConstants.F2W_CENTER_NAME.x, GameTwoConstants.F2W_CENTER_NAME.y, GameTwoConstants.MODE_BUBBLE_RADIUS);

        renderer.setColor(GameTwoConstants.W2F_COLOR);
        renderer.circle(GameTwoConstants.W2F_CENTER_NAME.x, GameTwoConstants.W2F_CENTER_NAME.y, GameTwoConstants.MODE_BUBBLE_RADIUS);

        renderer.setColor(GameTwoConstants.F2W_COLOR);
        renderer.circle(GameTwoConstants.F2W_CENTER_ATTR.x, GameTwoConstants.F2W_CENTER_ATTR.y, GameTwoConstants.MODE_BUBBLE_RADIUS);

        renderer.setColor(GameTwoConstants.W2F_COLOR);
        renderer.circle(GameTwoConstants.W2F_CENTER_ATTR.x, GameTwoConstants.W2F_CENTER_ATTR.y, GameTwoConstants.MODE_BUBBLE_RADIUS);


        renderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.getData().setScale(.2f);
        font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

        final GlyphLayout mode_one_second = new GlyphLayout(font, GameTwoConstants.MODE_ONE_SECOND);
        font.draw(batch, GameTwoConstants.MODE_ONE_SECOND, GameTwoConstants.F2W_CENTER_NAME.x, GameTwoConstants.F2W_CENTER_NAME.y + mode_one_second.height / 3, 0, Align.center, false);

        final GlyphLayout mode_one_first = new GlyphLayout(font, GameTwoConstants.MODE_ONE_FIRST);
        font.draw(batch, GameTwoConstants.MODE_ONE_FIRST, GameTwoConstants.F2W_CENTER_NAME.x,
                GameTwoConstants.F2W_CENTER_NAME.y + mode_one_first.height / 3 + mode_one_second.height, 0, Align.center, false);


        final GlyphLayout mode_one_third = new GlyphLayout(font, GameTwoConstants.MODE_ONE_THIRD);
        font.draw(batch, GameTwoConstants.MODE_ONE_THIRD, GameTwoConstants.F2W_CENTER_ATTR.x, GameTwoConstants.F2W_CENTER_ATTR.y + mode_one_third.height / 3, 0, Align.center, false);

        final GlyphLayout mode_one_first_two = new GlyphLayout(font, GameTwoConstants.MODE_ONE_FIRST);
        font.draw(batch, GameTwoConstants.MODE_ONE_FIRST, GameTwoConstants.F2W_CENTER_ATTR.x,
                GameTwoConstants.F2W_CENTER_ATTR.y + mode_one_first_two.height / 3 + mode_one_third.height, 0, Align.center, false);




        final GlyphLayout mode_two_second = new GlyphLayout(font, GameTwoConstants.MODE_TWO_SECOND);
        font.draw(batch, GameTwoConstants.MODE_TWO_SECOND, GameTwoConstants.W2F_CENTER_NAME.x, GameTwoConstants.W2F_CENTER_NAME.y + mode_two_second.height / 3, 0, Align.center, false);


        final GlyphLayout mode_two_first = new GlyphLayout(font, GameTwoConstants.MODE_TWO_FIRST);
        font.draw(batch, GameTwoConstants.MODE_TWO_FIRST, GameTwoConstants.W2F_CENTER_NAME.x,
                GameTwoConstants.W2F_CENTER_NAME.y + mode_two_first.height / 3 + mode_two_second.height, 0, Align.center, false);



        final GlyphLayout mode_two_second_two = new GlyphLayout(font, GameTwoConstants.MODE_TWO_SECOND);
        font.draw(batch, GameTwoConstants.MODE_TWO_SECOND, GameTwoConstants.W2F_CENTER_ATTR.x, GameTwoConstants.W2F_CENTER_ATTR.y + mode_two_second_two.height / 3, 0, Align.center, false);


        final GlyphLayout mode_two_third = new GlyphLayout(font, GameTwoConstants.MODE_TWO_THIRD);
        font.draw(batch, GameTwoConstants.MODE_TWO_THIRD, GameTwoConstants.W2F_CENTER_ATTR.x,
                GameTwoConstants.W2F_CENTER_ATTR.y + mode_two_third.height / 3 + mode_two_second_two.height, 0, Align.center, false);



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

        if (worldTouch.dst(GameTwoConstants.F2W_CENTER_NAME) < GameTwoConstants.MODE_BUBBLE_RADIUS) {
            game.setScreen(new FaceToNameScreen(game, 0, 1, GameTwoConstants.MODE_NAME));
        }

        if (worldTouch.dst(GameTwoConstants.F2W_CENTER_ATTR) < GameTwoConstants.MODE_BUBBLE_RADIUS) {
            game.setScreen(new FaceToNameScreen(game, 0, 1, GameTwoConstants.MODE_ATTR));
        }

        if (worldTouch.dst(GameTwoConstants.W2F_CENTER_NAME) < GameTwoConstants.MODE_BUBBLE_RADIUS) {
            game.setScreen(new NameToFaceScreen(game, 0, 1, GameTwoConstants.MODE_NAME));
        }

        if (worldTouch.dst(GameTwoConstants.W2F_CENTER_ATTR) < GameTwoConstants.MODE_BUBBLE_RADIUS) {
            game.setScreen(new NameToFaceScreen(game, 0, 1, GameTwoConstants.MODE_ATTR));
        }


        return true;
    }
}
