package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DifficultyScreen extends InputAdapter implements Screen {
    public static final String TAG = DifficultyScreen.class.getName();

    WorkingMemoryGame memGame;
    ObjectRecognitionGame obGame;
    SpatialMemoryGame spaGame;
    boolean isMem, isSpa, isOb = false;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    Texture background;

    BitmapFont font;
    private float elapsed;

    public DifficultyScreen(WorkingMemoryGame game) {
        isMem = true;
        this.memGame = game;
    }
    public DifficultyScreen(SpatialMemoryGame game) {
        isSpa = true;
        this.spaGame = game;
    }
    public DifficultyScreen(ObjectRecognitionGame game) {
        isOb = true;
        this.obGame = game;
    }




    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(BlockGameConstants.DIFFICULTY_WORLD_SIZE, BlockGameConstants.DIFFICULTY_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(BlockGameConstants.DIFFICULTY_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;


        if(isMem){
            viewport.apply();
            Gdx.gl.glClearColor(BlockGameConstants.BACKGROUND_COLOR.r, BlockGameConstants.BACKGROUND_COLOR.g, BlockGameConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(BlockGameConstants.EASY_COLOR);
            renderer.circle(BlockGameConstants.EASY_CENTER.x, BlockGameConstants.EASY_CENTER.y, BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(BlockGameConstants.MEDIUM_COLOR);
            renderer.circle(BlockGameConstants.MEDIUM_CENTER.x, BlockGameConstants.MEDIUM_CENTER.y, BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(BlockGameConstants.HARD_COLOR);
            renderer.circle(BlockGameConstants.HARD_CENTER.x, BlockGameConstants.HARD_CENTER.y, BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(.2f);
            font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

            final GlyphLayout easyLayout = new GlyphLayout(font, BlockGameConstants.EASY_LABEL);
            font.draw(batch, BlockGameConstants.EASY_LABEL, BlockGameConstants.EASY_CENTER.x, BlockGameConstants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

            final GlyphLayout mediumLayout = new GlyphLayout(font, BlockGameConstants.MEDIUM_LABEL);
            font.draw(batch, BlockGameConstants.MEDIUM_LABEL, BlockGameConstants.MEDIUM_CENTER.x, BlockGameConstants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

            final GlyphLayout hardLayout = new GlyphLayout(font, BlockGameConstants.HARD_LABEL);
            font.draw(batch, BlockGameConstants.HARD_LABEL, BlockGameConstants.HARD_CENTER.x, BlockGameConstants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

            batch.end();
        } else if(isOb) {
            viewport.apply();
            Gdx.gl.glClearColor(ObjectGameConstants.BACKGROUND_COLOR.r, ObjectGameConstants.BACKGROUND_COLOR.g, ObjectGameConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(ObjectGameConstants.EASY_COLOR);
            renderer.circle(ObjectGameConstants.EASY_CENTER.x, ObjectGameConstants.EASY_CENTER.y, ObjectGameConstants.MODE_BUBBLE_RADIUS);

            renderer.setColor(ObjectGameConstants.MEDIUM_COLOR);
            renderer.circle(ObjectGameConstants.MEDIUM_CENTER.x, ObjectGameConstants.MEDIUM_CENTER.y, ObjectGameConstants.MODE_BUBBLE_RADIUS);

            renderer.setColor(ObjectGameConstants.HARD_COLOR);
            renderer.circle(ObjectGameConstants.HARD_CENTER.x, ObjectGameConstants.HARD_CENTER.y, ObjectGameConstants.MODE_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(ObjectGameConstants.MODE_LABEL_SCALE);
            font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

            final GlyphLayout easyLayout = new GlyphLayout(font, BlockGameConstants.EASY_LABEL);
            font.draw(batch, BlockGameConstants.EASY_LABEL, ObjectGameConstants.EASY_CENTER.x, ObjectGameConstants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

            final GlyphLayout mediumLayout = new GlyphLayout(font, BlockGameConstants.MEDIUM_LABEL);
            font.draw(batch, BlockGameConstants.MEDIUM_LABEL, ObjectGameConstants.MEDIUM_CENTER.x, ObjectGameConstants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

            final GlyphLayout hardLayout = new GlyphLayout(font, BlockGameConstants.HARD_LABEL);
            font.draw(batch, BlockGameConstants.HARD_LABEL, ObjectGameConstants.HARD_CENTER.x, ObjectGameConstants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

            batch.end();
        } else if(isSpa) {
            Gdx.gl.glClearColor(SpatialGameConstants.BACKGROUND_COLOR.r, SpatialGameConstants.BACKGROUND_COLOR.g, SpatialGameConstants.BACKGROUND_COLOR.b, 1);
            //Gdx.gl.glClearColor(0.65f, 0.81f, 0.91f, 1);

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.setProjectionMatrix(viewport.getCamera().combined);


            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(SpatialGameConstants.EASY_COLOR);
            renderer.circle(BlockGameConstants.EASY_CENTER.x, BlockGameConstants.EASY_CENTER.y, BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(SpatialGameConstants.MEDIUM_COLOR);
            renderer.circle(BlockGameConstants.MEDIUM_CENTER.x, BlockGameConstants.MEDIUM_CENTER.y, BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.setColor(SpatialGameConstants.HARD_COLOR);
            renderer.circle(BlockGameConstants.HARD_CENTER.x, BlockGameConstants.HARD_CENTER.y, BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS);

            renderer.end();

            batch.setProjectionMatrix(viewport.getCamera().combined);

            batch.begin();
            font.getData().setScale(.2f);
            font.setColor(Color.WHITE);

            final GlyphLayout easyLayout = new GlyphLayout(font, BlockGameConstants.EASY_LABEL);
            font.draw(batch, BlockGameConstants.EASY_LABEL, BlockGameConstants.EASY_CENTER.x, BlockGameConstants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

            final GlyphLayout mediumLayout = new GlyphLayout(font, BlockGameConstants.MEDIUM_LABEL);
            font.draw(batch, BlockGameConstants.MEDIUM_LABEL, BlockGameConstants.MEDIUM_CENTER.x, BlockGameConstants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

            final GlyphLayout hardLayout = new GlyphLayout(font, BlockGameConstants.HARD_LABEL);
            font.draw(batch, BlockGameConstants.HARD_LABEL, BlockGameConstants.HARD_CENTER.x, BlockGameConstants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

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
            if (worldTouch.dst(BlockGameConstants.EASY_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.actionResolver.setDifficulty(BlockGameConstants.DIFFICULTY_EASY);
                memGame.setScreen(new MemoryScreen(memGame, BlockGameConstants.DIFFICULTY_EASY,0,1));
            }

            if (worldTouch.dst(BlockGameConstants.MEDIUM_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.actionResolver.setDifficulty(BlockGameConstants.DIFFICULTY_MEDIUM);
                memGame.setScreen(new MemoryScreen(memGame, BlockGameConstants.DIFFICULTY_MEDIUM,0,1));
            }

            if (worldTouch.dst(BlockGameConstants.HARD_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.actionResolver.setDifficulty(BlockGameConstants.DIFFICULTY_HARD);
                memGame.setScreen(new MemoryScreen(memGame, BlockGameConstants.DIFFICULTY_HARD,0,1));
            }
        } else if(isOb){
            if (worldTouch.dst(BlockGameConstants.EASY_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                obGame.actionResolver.setDifficulty(ObjectGameConstants.DIFFICULTY_EASY);
                obGame.setScreen(new ObjectRecognitionScreen(obGame, ObjectGameConstants.DIFFICULTY_EASY));
            }

            if (worldTouch.dst(BlockGameConstants.MEDIUM_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                obGame.actionResolver.setDifficulty(ObjectGameConstants.DIFFICULTY_MEDIUM);
                obGame.setScreen(new ObjectRecognitionScreen(obGame, ObjectGameConstants.DIFFICULTY_MEDIUM));
            }

            if (worldTouch.dst(BlockGameConstants.HARD_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                obGame.actionResolver.setDifficulty(ObjectGameConstants.DIFFICULTY_HARD);
                obGame.setScreen(new ObjectRecognitionScreen(obGame, ObjectGameConstants.DIFFICULTY_HARD));
            }
        } else if(isSpa){
            if (worldTouch.dst(BlockGameConstants.EASY_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.actionResolver.setDifficulty(SpatialGameConstants.DIFFICULTY_EASY);
                spaGame.setScreen(new SpatialScreen(spaGame, SpatialGameConstants.DIFFICULTY_EASY));
            }

            if (worldTouch.dst(BlockGameConstants.MEDIUM_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.actionResolver.setDifficulty(SpatialGameConstants.DIFFICULTY_MEDIUM);
                spaGame.setScreen(new SpatialScreen(spaGame,  SpatialGameConstants.DIFFICULTY_MEDIUM));
            }

            if (worldTouch.dst(BlockGameConstants.HARD_CENTER) < BlockGameConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.actionResolver.setDifficulty(SpatialGameConstants.DIFFICULTY_HARD);
                spaGame.setScreen(new SpatialScreen(spaGame,  SpatialGameConstants.DIFFICULTY_HARD));
            }
        }

        return false;
    }
}