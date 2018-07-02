package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;

import static com.lucidity.game.Constants.BACKGROUND_COLOR;

/**
 * Created by lixiaoyan on 6/29/18.
 */

//TODO: properly dispose screen
public class EndScreen extends InputAdapter implements Screen {
    public static final String TAG = EndScreen.class.getName();
    MemoryGame game;

    ExtendViewport memoryViewport;
    ScreenViewport hudViewport;


    int screenWidth;
    int screenHeight;

    int score;
    int trial;

    private SpriteBatch batch;
    public BitmapFont font;

    public EndScreen(MemoryGame game, int points, int trials) {
        this.game = game;
        this.score = points;
        this.trial = trials;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
    }

    @Override
    public void show() {
        memoryViewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        hudViewport = new ScreenViewport();
    }
    @Override
    public void resize(int width, int height) {
        memoryViewport.update(width, height, true);
        hudViewport.update(width, height, true);
        System.out.print("resize");

    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }

    @Override
    public void render(float delta) {

        memoryViewport.apply(true);
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.setColor(Color.valueOf("#9FEDD7"));
        font.getData().setScale(4f);
        font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

        font.draw(batch, "Your score is " + Integer.toString(score) + "/" + Integer.toString(trial),screenWidth/4, screenHeight/2);
        batch.end();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.exit();
        return true;
    }

}
