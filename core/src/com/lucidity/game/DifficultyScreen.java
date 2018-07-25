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

    WorkingMemoryGame memGame;
    SpacialMemoryGame spaGame;
    boolean isMem, isSpa = false;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;
    private float elapsed;

    public DifficultyScreen(WorkingMemoryGame game) {
        isMem = true;
        this.memGame = game;
    }
    public DifficultyScreen(SpacialMemoryGame game) {
        isSpa = true;
        this.spaGame = game;
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


        if(isMem){
            if(elapsed < 4) {
                Gdx.gl.glClearColor(1.0f,0.98f,0.78f, 1);
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                batch.begin();
                    font.setColor(new Color(0.01f, 0.4f, 0.44f, 1));
                    font.getData().setScale(GameOneConstants.TITLE_SCALE);

                    final GlyphLayout promptLayout_two = new GlyphLayout(font, GameOneConstants.TITLE_TWO);
                    font.draw(batch, promptLayout_two, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) /2,
                            GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);


                    final GlyphLayout promptLayout_one = new GlyphLayout(font, GameOneConstants.TITLE_ONE);
                    font.draw(batch, promptLayout_one, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) / 2,
                            GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height);


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
        } else if (isSpa) {
            Gdx.gl.glClearColor(GameFourConstants.BACKGROUND_COLOR.r, GameFourConstants.BACKGROUND_COLOR.g, GameFourConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(GameFourConstants.EASY_COLOR);
            renderer.circle(GameOneConstants.EASY_CENTER.x, GameOneConstants.EASY_CENTER.y, GameOneConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(GameFourConstants.MEDIUM_COLOR);
            renderer.circle(GameOneConstants.MEDIUM_CENTER.x, GameOneConstants.MEDIUM_CENTER.y, GameOneConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(GameFourConstants.HARD_COLOR);
            renderer.circle(GameOneConstants.HARD_CENTER.x, GameOneConstants.HARD_CENTER.y, GameOneConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(1f);
            font.setColor(Color.WHITE);

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

        if(isMem) {
            if (worldTouch.dst(GameOneConstants.EASY_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.showMemoryScreen(GameOneConstants.Difficulty.EASY);
            }

            if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.showMemoryScreen(GameOneConstants.Difficulty.MEDIUM);
            }

            if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.showMemoryScreen(GameOneConstants.Difficulty.HARD);
            }
        } else if(isSpa){
            if (worldTouch.dst(GameOneConstants.EASY_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.setScreen(new SpacialScreen(spaGame, GameFourConstants.DIFFICULTY_EASY));
            }

            if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.setScreen(new SpacialScreen(spaGame,  GameFourConstants.DIFFICULTY_MEDIUM));
            }

            if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.setScreen(new SpacialScreen(spaGame,  GameFourConstants.DIFFICULTY_HARD));
            }
        }

        return true;
    }

}