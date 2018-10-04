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
    TextureRegion textureRegion;
    Sprite resizedBg;

    BitmapFont font;
    private float elapsed;

    public DifficultyScreen(WorkingMemoryGame game) {
        isMem = true;
        this.memGame = game;
    }
    public DifficultyScreen(SpatialMemoryGame game) {
        isSpa = true;
        this.spaGame = game;

        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        textureRegion= new TextureRegion(background, 0, 0, background.getWidth(), background.getHeight());
        resizedBg = new Sprite(textureRegion);
        resizedBg.setSize(1f,  resizedBg.getHeight() / resizedBg.getWidth());


    }
    public DifficultyScreen(ObjectRecognitionGame game) {
        isOb = true;
        this.obGame = game;
    }


    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameOneConstants.DIFFICULTY_WORLD_SIZE, GameOneConstants.DIFFICULTY_WORLD_SIZE);
        Gdx.input.setInputProcessor(this);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(GameOneConstants.DIFFICULTY_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;


        if(isMem){
            viewport.apply();
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
            font.getData().setScale(.2f);
            font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

            final GlyphLayout easyLayout = new GlyphLayout(font, GameOneConstants.EASY_LABEL);
            font.draw(batch, GameOneConstants.EASY_LABEL, GameOneConstants.EASY_CENTER.x, GameOneConstants.EASY_CENTER.y + easyLayout.height / 2, 0, Align.center, false);

            final GlyphLayout mediumLayout = new GlyphLayout(font, GameOneConstants.MEDIUM_LABEL);
            font.draw(batch, GameOneConstants.MEDIUM_LABEL, GameOneConstants.MEDIUM_CENTER.x, GameOneConstants.MEDIUM_CENTER.y + mediumLayout.height / 2, 0, Align.center, false);

            final GlyphLayout hardLayout = new GlyphLayout(font, GameOneConstants.HARD_LABEL);
            font.draw(batch, GameOneConstants.HARD_LABEL, GameOneConstants.HARD_CENTER.x, GameOneConstants.HARD_CENTER.y + hardLayout.height / 2, 0, Align.center, false);

            batch.end();
        } else if(isOb) {
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
        } else if(isSpa) {
            Gdx.gl.glClearColor(GameFourConstants.BACKGROUND_COLOR.r, GameFourConstants.BACKGROUND_COLOR.g, GameFourConstants.BACKGROUND_COLOR.b, 1);
            //Gdx.gl.glClearColor(0.65f, 0.81f, 0.91f, 1);

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            //TODO: This is butchered, fixed background image
            /*batch.begin();
            batch.draw(background, 0, 0, viewport.getCamera().viewportWidth, viewport.getCamera().viewportHeight);
            batch.end();*/
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
            font.getData().setScale(.2f);
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
                memGame.actionResolver.setDifficulty(GameOneConstants.DIFFICULTY_EASY);
                memGame.setScreen(new MemoryScreen(memGame, GameOneConstants.DIFFICULTY_EASY,0,1));
            }

            if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.actionResolver.setDifficulty(GameOneConstants.DIFFICULTY_MEDIUM);
                memGame.setScreen(new MemoryScreen(memGame, GameOneConstants.DIFFICULTY_MEDIUM,0,1));
            }

            if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                memGame.actionResolver.setDifficulty(GameOneConstants.DIFFICULTY_HARD);
                memGame.setScreen(new MemoryScreen(memGame, GameOneConstants.DIFFICULTY_HARD,0,1));
            }
        } else if(isOb){
            if (worldTouch.dst(GameOneConstants.EASY_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                obGame.actionResolver.setDifficulty(GameThreeConstants.DIFFICULTY_EASY);
                obGame.setScreen(new ObjectRecognitionScreen(obGame, GameThreeConstants.DIFFICULTY_EASY));
            }

            if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                obGame.actionResolver.setDifficulty(GameThreeConstants.DIFFICULTY_MEDIUM);
                obGame.setScreen(new ObjectRecognitionScreen(obGame, GameThreeConstants.DIFFICULTY_MEDIUM));
            }

            if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                obGame.actionResolver.setDifficulty(GameThreeConstants.DIFFICULTY_HARD);
                obGame.setScreen(new ObjectRecognitionScreen(obGame, GameThreeConstants.DIFFICULTY_HARD));
            }
        } else if(isSpa){
            if (worldTouch.dst(GameOneConstants.EASY_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.actionResolver.setDifficulty(GameFourConstants.DIFFICULTY_EASY);
                spaGame.setScreen(new SpatialScreen(spaGame, GameFourConstants.DIFFICULTY_EASY));
            }

            if (worldTouch.dst(GameOneConstants.MEDIUM_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.actionResolver.setDifficulty(GameFourConstants.DIFFICULTY_MEDIUM);
                spaGame.setScreen(new SpatialScreen(spaGame,  GameFourConstants.DIFFICULTY_MEDIUM));
            }

            if (worldTouch.dst(GameOneConstants.HARD_CENTER) < GameOneConstants.DIFFICULTY_BUBBLE_RADIUS) {
                spaGame.actionResolver.setDifficulty(GameFourConstants.DIFFICULTY_HARD);
                spaGame.setScreen(new SpatialScreen(spaGame,  GameFourConstants.DIFFICULTY_HARD));
            }
        }

        return false;
    }
}