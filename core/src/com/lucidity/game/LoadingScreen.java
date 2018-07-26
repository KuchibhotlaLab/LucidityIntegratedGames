package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by lixiaoyan on 7/6/18.
 */

public class LoadingScreen implements Screen {
    private FacialMemoryGame FacMemGame;
    private ObjectRecognitionGame ObjRecGame;
    private SpacialMemoryGame SpMemGame;
    boolean isFacGame, isObjGame, isSpGame = false;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;

    BitmapFont font;
    float elapsed;

    public LoadingScreen(FacialMemoryGame game) {
        isFacGame = true;
        this.FacMemGame = game;
    }
    public LoadingScreen(ObjectRecognitionGame game) {
        isObjGame = true;
        this.ObjRecGame = game;
    }

    public LoadingScreen(SpacialMemoryGame game) {
        isSpGame = true;
        this.SpMemGame = game;
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameTwoConstants.MODE_WORLD_SIZE, GameTwoConstants.MODE_WORLD_SIZE);

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
        font.getData().setScale(GameTwoConstants.MODE_LABEL_SCALE);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        viewport.apply();
        if(isFacGame) {
            Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        } else if(isObjGame){
            Gdx.gl.glClearColor(GameThreeConstants.LOADING_COLOR.r, GameThreeConstants.LOADING_COLOR.g, GameThreeConstants.LOADING_COLOR.b, 1);
        } else if(isSpGame){
            //Gdx.gl.glClearColor(0.54f, 0.75f, 0.97f, 1);
            Gdx.gl.glClearColor(GameFourConstants.LOADING_COLOR.r, GameFourConstants.LOADING_COLOR.g, GameFourConstants.LOADING_COLOR.b, 1);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (elapsed < 2) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


            batch.begin();
            font.getData().setScale(GameTwoConstants.TITLE_SCALE);

            if(isFacGame){
                font.setColor(GameTwoConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameTwoConstants.TITLE_THREE);
                font.draw(batch, promptLayout_three, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width) / 2,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameTwoConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2 / 3,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameTwoConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) * 5 / 4,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);
            } else if(isObjGame){
                font.setColor(GameThreeConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.TITLE_THREE);
                font.draw(batch, promptLayout_three, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width) * 2/3,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameThreeConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2 / 3,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) * 8 / 3,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);
            } else if (isSpGame){
                font.setColor(GameFourConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameFourConstants.TITLE_TWO);
                font.draw(batch, promptLayout_three, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width) * 3 / 2,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameFourConstants.TITLE_ONE);
                font.draw(batch, promptLayout_two, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);
            }

            batch.end();
        } else {
            if(isFacGame){
                FacMemGame.setScreen(new ModeScreen(FacMemGame));
            } else if(isObjGame) {
                ObjRecGame.setScreen(new ObjectRecognitionDifficultyScreen(ObjRecGame));
            } else if(isSpGame){
                SpMemGame.setScreen(new DifficultyScreen(SpMemGame));
            }

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
}
