package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;

/**
 * Created by lixiaoyan on 7/5/18.
 */

public class NameToFaceScreen extends InputAdapter implements Screen {
    private FacialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth, screenHeight;
    int score, trial;

    ShapeRenderer renderer;

    Texture face;
    Sprite sprite;
    SpriteBatch batch;
    BitmapFont font;

    String name;
    String attribute;

    float elapsed;

    public NameToFaceScreen(FacialMemoryGame game, int points, int trials) {
        this.game = game;
        score = points;
        trial = trials;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);


        generateTrial();

    }
    @Override
    public void show() {
        viewport = new ExtendViewport(GameTwoConstants.WORLD_SIZE, GameTwoConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render(float delta) {
        viewport.apply(true);
        Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;
        if(elapsed < 2) {
            batch.begin();
            font.getData().setScale(GameTwoConstants.PROMPT_SCALE);

            final GlyphLayout promptLayout_three = new GlyphLayout(font, GameTwoConstants.PROMPT_FOUR);
            font.draw(batch, promptLayout_three, (screenWidth- promptLayout_three.width) / 2,
                    screenHeight/2);

            final GlyphLayout promptLayout_two = new GlyphLayout(font, GameTwoConstants.PROMPT_THREE + attribute);
            font.draw(batch, promptLayout_two, (screenWidth- promptLayout_two.width) / 2,
                    screenHeight/2 + 1.5f * promptLayout_three.height);


            final GlyphLayout promptLayout_one = new GlyphLayout(font, GameTwoConstants.PROMPT_ONE);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                    screenHeight/2 + 1.5f * promptLayout_three.height + 1.5f * promptLayout_two.height);



            batch.end();

        } else {


            batch.begin();
            font.getData().setScale(GameTwoConstants.PROMPT_SCALE);
            final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.PROMPT + name);
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight * 7 / 8);


            font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
            font.draw(batch, GameTwoConstants.SCORE_LABEL + Integer.toString(score),
                    GameTwoConstants.SCORE_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);

            font.draw(batch, GameTwoConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameTwoConstants.TRIAL_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);



            //batch.draw(sprite, (screenWidth - sprite.getWidth())/2, (screenHeight - sprite.getHeight())/2);
            batch.draw(sprite, (screenWidth - sprite.getWidth()/6)/2, (screenHeight - sprite.getHeight()/6)/2,
                    sprite.getWidth()/6, sprite.getHeight()/6);
            //sprite.setPosition((screenWidth - face.getWidth())/2, (screenHeight - face.getHeight())/2,);
            //sprite.draw(batch);
            batch.end();
        }
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
        batch.dispose();
        font.dispose();
        renderer.dispose();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    private void generateTrial(){
        name = "Jane Doe";
        attribute = "name";

        //TODO: randomize picture
        String locRoot = Gdx.files.getLocalStoragePath();
        File folder = new File(locRoot);
        File[] listOfFiles = folder.listFiles();


        face = new Texture(Gdx.files.absolute(listOfFiles[1].getPath()));

        sprite = new Sprite(face); // Creates a sprite from a Texture
        //sprite.scale(0.1f); // Scale the sprite
        //sprite.setScale(0.5f, 0.5f);

    }

}
