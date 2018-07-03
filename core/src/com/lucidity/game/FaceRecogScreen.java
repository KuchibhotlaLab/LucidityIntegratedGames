package com.lucidity.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class FaceRecogScreen extends InputAdapter implements Screen {
    private FacialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth;
    int screenHeight;

    ShapeRenderer renderer;

    Rectangle answer1, answer2;

    Texture face;
    SpriteBatch batch;
    BitmapFont font;

    public FaceRecogScreen(FacialMemoryGame game) {
        this.game = game;
        answer1 = answer2 = new Rectangle();
    }
    @Override
    public void show() {
        viewport = new ExtendViewport(GameOneConstants.WORLD_SIZE, GameOneConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();


        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        renderer = new ShapeRenderer();
        File folder = new File("/");
        File[] listOfFiles = folder.listFiles();

        //face = new Texture(Gdx.files.external(listOfFiles[0].getPath()));
        face = new Texture(Gdx.files.internal("test.jpg"));
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);

        Gdx.input.setInputProcessor(this);

        answer1.height = answer2.height = screenHeight / 12;
        answer1.width = answer2.width = screenWidth / 3;
        answer1.x = answer2.x =screenWidth / 3;
        answer1.y = screenHeight / 6;
        answer2.y = answer1.y + answer1.height;

    }

    @Override
    public void render(float delta) {
        viewport.apply(true);
        Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setProjectionMatrix(viewport.getCamera().combined);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(GameTwoConstants.CHOICE_COLOR);
        renderer.rect(answer1.x, answer1.y, answer1.getWidth(), answer1.getHeight());
        renderer.rect(answer2.x, answer2.y, answer2.getWidth(), answer2.getHeight());

        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);

        renderer.setColor(GameTwoConstants.OUTLINE_COLOR);
        renderer.rect(answer1.x, answer1.y, answer1.getWidth(), answer1.getHeight());
        renderer.rect(answer2.x, answer2.y, answer2.getWidth(), answer2.getHeight());

        renderer.end();


        batch.begin();
        font.getData().setScale(GameTwoConstants.PROMPT_SCALE);
        final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.PROMPT);
        font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight * 7 / 8);

        batch.draw(face, (screenWidth - face.getWidth())/2, (screenHeight - face.getHeight())/2);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
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
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return true;
    }

}
