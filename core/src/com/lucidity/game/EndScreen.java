package com.lucidity.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.lucidity.game.GameOneConstants.BACKGROUND_COLOR;

/**
 * Created by lixiaoyan on 6/29/18.
 */


//TODO: properly dispose screen
public class EndScreen extends InputAdapter implements Screen {
    public static final String TAG = EndScreen.class.getName();
    WorkingMemoryGame gameIndep;
    FacialMemoryGame gameDep;
    ObjectRecognitionGame gameOb;

    ExtendViewport memoryViewport;
    ScreenViewport hudViewport;

    boolean isGameOne, isGameTwo, isGameThree = false;


    int screenWidth;
    int screenHeight;

    int score;
    int trial;

    private SpriteBatch batch;
    public BitmapFont font;

    private boolean exit = false;

    public EndScreen(WorkingMemoryGame game, int points, int trials) {
        this.gameIndep = game;
        this.score = points;
        this.trial = trials;
        isGameOne = true;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        memoryViewport = new ExtendViewport(GameOneConstants.WORLD_SIZE, GameOneConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
    }


    public EndScreen(FacialMemoryGame game, int points, int trials) {
        this.gameDep = game;
        this.score = points;
        this.trial = trials;
        isGameTwo = true;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        memoryViewport = new ExtendViewport(GameTwoConstants.WORLD_SIZE, GameTwoConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
    }

    public EndScreen(ObjectRecognitionGame game, int points, int trials) {
        this.gameOb = game;
        this.score = points;
        this.trial = trials;
        isGameThree = true;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        memoryViewport = new ExtendViewport(GameThreeConstants.WORLD_SIZE, GameThreeConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
    }



    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        memoryViewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void render(float delta) {
        memoryViewport.apply(true);

        if(isGameOne){
            Gdx.gl.glClearColor(1.0f,0.98f,0.78f, 1);
        } else if(isGameTwo) {
            Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameThree) {
            Gdx.gl.glClearColor(GameThreeConstants.BACKGROUND_COLOR.r, GameThreeConstants.BACKGROUND_COLOR.g, GameThreeConstants.BACKGROUND_COLOR.b, 1);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.getData().setScale(4f);
        final GlyphLayout promptLayout = new GlyphLayout(font, "Your score is " + Integer.toString(score) + "/" + Integer.toString(trial));
        font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2,
                screenHeight / 2);
        batch.end();

        if(exit){
            Gdx.app.exit();
        }

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        font.dispose();
        batch.dispose();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //TODO: return to where the game came from
        gameIndep.actionResolver.FaceGame();
        gameDep.actionResolver.ObjectGame();
        gameOb.actionResolver.MemoryGame();
        return false;
    }

}
