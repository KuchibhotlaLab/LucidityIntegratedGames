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

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class ModeScreen extends InputAdapter implements Screen {

    private FacialMemoryGame facialMemoryGame;
    private RecallGame recallGame;

    private boolean isFacialGame, isRecallGame = false;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;
    float elapsed;

    public ModeScreen(FacialMemoryGame game) {
        this.facialMemoryGame = game;
        isFacialGame = true;
    }

    public ModeScreen(RecallGame game) {
        this.recallGame = game;
        isRecallGame = true;
    }
    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(FacialGameConstants.MODE_WORLD_SIZE, FacialGameConstants.MODE_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(FacialGameConstants.MODE_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        viewport.apply();
        if(isFacialGame) {
            Gdx.gl.glClearColor(FacialGameConstants.BACKGROUND_COLOR.r, FacialGameConstants.BACKGROUND_COLOR.g, FacialGameConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(FacialGameConstants.F2W_COLOR);
            renderer.circle(FacialGameConstants.F2W_CENTER_NAME.x, FacialGameConstants.F2W_CENTER_NAME.y, FacialGameConstants.MODE_BUBBLE_RADIUS);

            renderer.setColor(FacialGameConstants.W2F_COLOR);
            renderer.circle(FacialGameConstants.W2F_CENTER_NAME.x, FacialGameConstants.W2F_CENTER_NAME.y, FacialGameConstants.MODE_BUBBLE_RADIUS);

            renderer.setColor(FacialGameConstants.F2W_COLOR);
            renderer.circle(FacialGameConstants.F2W_CENTER_ATTR.x, FacialGameConstants.F2W_CENTER_ATTR.y, FacialGameConstants.MODE_BUBBLE_RADIUS);

            renderer.setColor(FacialGameConstants.W2F_COLOR);
            renderer.circle(FacialGameConstants.W2F_CENTER_ATTR.x, FacialGameConstants.W2F_CENTER_ATTR.y, FacialGameConstants.MODE_BUBBLE_RADIUS);


            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(.2f);
            font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

            final GlyphLayout mode_one_second = new GlyphLayout(font, FacialGameConstants.MODE_ONE_SECOND);
            font.draw(batch, FacialGameConstants.MODE_ONE_SECOND, FacialGameConstants.F2W_CENTER_NAME.x, FacialGameConstants.F2W_CENTER_NAME.y + mode_one_second.height / 3, 0, Align.center, false);

            final GlyphLayout mode_one_first = new GlyphLayout(font, FacialGameConstants.MODE_ONE_FIRST);
            font.draw(batch, FacialGameConstants.MODE_ONE_FIRST, FacialGameConstants.F2W_CENTER_NAME.x,
                    FacialGameConstants.F2W_CENTER_NAME.y + mode_one_first.height / 3 + mode_one_second.height, 0, Align.center, false);


            final GlyphLayout mode_one_third = new GlyphLayout(font, FacialGameConstants.MODE_ONE_THIRD);
            font.draw(batch, FacialGameConstants.MODE_ONE_THIRD, FacialGameConstants.F2W_CENTER_ATTR.x, FacialGameConstants.F2W_CENTER_ATTR.y + mode_one_third.height / 3, 0, Align.center, false);

            final GlyphLayout mode_one_first_two = new GlyphLayout(font, FacialGameConstants.MODE_ONE_FIRST);
            font.draw(batch, FacialGameConstants.MODE_ONE_FIRST, FacialGameConstants.F2W_CENTER_ATTR.x,
                    FacialGameConstants.F2W_CENTER_ATTR.y + mode_one_first_two.height / 3 + mode_one_third.height, 0, Align.center, false);


            final GlyphLayout mode_two_second = new GlyphLayout(font, FacialGameConstants.MODE_TWO_SECOND);
            font.draw(batch, FacialGameConstants.MODE_TWO_SECOND, FacialGameConstants.W2F_CENTER_NAME.x, FacialGameConstants.W2F_CENTER_NAME.y + mode_two_second.height / 3, 0, Align.center, false);


            final GlyphLayout mode_two_first = new GlyphLayout(font, FacialGameConstants.MODE_TWO_FIRST);
            font.draw(batch, FacialGameConstants.MODE_TWO_FIRST, FacialGameConstants.W2F_CENTER_NAME.x,
                    FacialGameConstants.W2F_CENTER_NAME.y + mode_two_first.height / 3 + mode_two_second.height, 0, Align.center, false);


            final GlyphLayout mode_two_second_two = new GlyphLayout(font, FacialGameConstants.MODE_TWO_SECOND);
            font.draw(batch, FacialGameConstants.MODE_TWO_SECOND, FacialGameConstants.W2F_CENTER_ATTR.x, FacialGameConstants.W2F_CENTER_ATTR.y + mode_two_second_two.height / 3, 0, Align.center, false);


            final GlyphLayout mode_two_third = new GlyphLayout(font, FacialGameConstants.MODE_TWO_THIRD);
            font.draw(batch, FacialGameConstants.MODE_TWO_THIRD, FacialGameConstants.W2F_CENTER_ATTR.x,
                    FacialGameConstants.W2F_CENTER_ATTR.y + mode_two_third.height / 3 + mode_two_second_two.height, 0, Align.center, false);
            batch.end();
        } else if(isRecallGame){
            Gdx.gl.glClearColor(RecallGameConstants.BACKGROUND_COLOR.r, RecallGameConstants.BACKGROUND_COLOR.g, RecallGameConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(RecallGameConstants.LOCATION_COLOR);
            renderer.circle(RecallGameConstants.LOCATION_CENTER_NAME.x, RecallGameConstants.LOCATION_CENTER_NAME.y, RecallGameConstants.MODE_BUBBLE_RADIUS);

            renderer.setColor(RecallGameConstants.RELATION_COLOR);
            renderer.circle(RecallGameConstants.RELATION_CENTER_NAME.x, RecallGameConstants.RELATION_CENTER_NAME.y, RecallGameConstants.MODE_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(.2f);
            font.setColor(Color.WHITE);

            final GlyphLayout mode_one_second = new GlyphLayout(font, RecallGameConstants.MODE_ONE_SECOND);
            font.draw(batch, RecallGameConstants.MODE_ONE_SECOND, RecallGameConstants.LOCATION_CENTER_NAME.x, RecallGameConstants.LOCATION_CENTER_NAME.y + mode_one_second.height / 3, 0, Align.center, false);

            final GlyphLayout mode_one_first = new GlyphLayout(font, RecallGameConstants.MODE_ONE);
            font.draw(batch, RecallGameConstants.MODE_ONE, RecallGameConstants.LOCATION_CENTER_NAME.x,
                    RecallGameConstants.LOCATION_CENTER_NAME.y + mode_one_first.height / 3 + mode_one_second.height, 0, Align.center, false);


            final GlyphLayout mode_one_third = new GlyphLayout(font, RecallGameConstants.MODE_ONE_THIRD);
            font.draw(batch, RecallGameConstants.MODE_ONE_THIRD, RecallGameConstants.RELATION_CENTER_NAME.x, RecallGameConstants.RELATION_CENTER_NAME.y + mode_one_third.height / 3, 0, Align.center, false);

            final GlyphLayout mode_one_first_two = new GlyphLayout(font, RecallGameConstants.MODE_ONE);
            font.draw(batch, RecallGameConstants.MODE_ONE, RecallGameConstants.RELATION_CENTER_NAME.x,
                    RecallGameConstants.RELATION_CENTER_NAME.y + mode_one_first_two.height / 3 + mode_one_third.height, 0, Align.center, false);

            batch.end();
        }
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

        if(isFacialGame) {
            if (worldTouch.dst(FacialGameConstants.F2W_CENTER_NAME) < FacialGameConstants.MODE_BUBBLE_RADIUS) {
                facialMemoryGame.setScreen(new FaceToNameScreen(facialMemoryGame, 0, 1, FacialGameConstants.MODE_NAME));
            }

            if (worldTouch.dst(FacialGameConstants.F2W_CENTER_ATTR) < FacialGameConstants.MODE_BUBBLE_RADIUS) {
                facialMemoryGame.setScreen(new FaceToNameScreen(facialMemoryGame, 0, 1, FacialGameConstants.MODE_ATTR));
            }

            if (worldTouch.dst(FacialGameConstants.W2F_CENTER_NAME) < FacialGameConstants.MODE_BUBBLE_RADIUS) {
                facialMemoryGame.setScreen(new NameToFaceScreen(facialMemoryGame, 0, 1, FacialGameConstants.MODE_NAME));
            }

            if (worldTouch.dst(FacialGameConstants.W2F_CENTER_ATTR) < FacialGameConstants.MODE_BUBBLE_RADIUS) {
                facialMemoryGame.setScreen(new NameToFaceScreen(facialMemoryGame, 0, 1, FacialGameConstants.MODE_ATTR));
            }
        } else if(isRecallGame){
            recallGame.setScreen(new RecallScreen(recallGame));
        }


        return true;
    }
}
