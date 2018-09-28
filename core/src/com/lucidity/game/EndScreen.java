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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by lixiaoyan on 6/29/18.
 */


public class EndScreen extends InputAdapter implements Screen {
    public static final String TAG = EndScreen.class.getName();
    WorkingMemoryGame gameIndep;
    FacialMemoryGame gameDep;
    ObjectRecognitionGame gameOb;
    SpatialMemoryGame gameSpa;

    ExtendViewport memoryViewport;
    ScreenViewport hudViewport;
    ShapeRenderer renderer;

    Texture background;
    TextureRegion textureRegion;
    Sprite resizedBg;

    boolean isGameOne, isGameTwo, isGameThree, isGameFour = false;


    int screenWidth;
    int screenHeight;

    int score;
    int trial;

    private SpriteBatch batch;
    public BitmapFont font;

    //private boolean exit = false;

    public EndScreen(WorkingMemoryGame game, int points, int trials) {
        this.gameIndep = game;
        this.score = points;
        this.trial = trials;
        isGameOne = true;
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));

        memoryViewport = new ExtendViewport(GameOneConstants.WORLD_SIZE, GameOneConstants.WORLD_SIZE);
    }


    public EndScreen(FacialMemoryGame game, int points, int trials) {
        this.gameDep = game;
        this.score = points;
        this.trial = trials;
        isGameTwo = true;
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));

        memoryViewport = new ExtendViewport(GameTwoConstants.WORLD_SIZE, GameTwoConstants.WORLD_SIZE);
    }

    public EndScreen(ObjectRecognitionGame game, int points, int trials) {
        this.gameOb = game;
        this.score = points;
        this.trial = trials;
        isGameThree = true;
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));

        memoryViewport = new ExtendViewport(GameThreeConstants.WORLD_SIZE, GameThreeConstants.WORLD_SIZE);
    }

    public EndScreen(SpatialMemoryGame game, int points, int trials) {
        this.gameSpa = game;
        this.score = points;
        this.trial = trials;
        isGameFour = true;
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));

        memoryViewport = new ExtendViewport(GameFourConstants.WORLD_SIZE, GameFourConstants.WORLD_SIZE);

    }



    @Override
    public void show() {

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        hudViewport = new ScreenViewport();

        textureRegion= new TextureRegion(background, 0, 0, background.getWidth(), background.getHeight());
        resizedBg = new Sprite(textureRegion);
        resizedBg.setSize(1f,  resizedBg.getHeight() / resizedBg.getWidth());

        renderer = new ShapeRenderer();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(isGameOne){
            Gdx.gl.glClearColor(1.0f,0.98f,0.78f, 1);
        } else if(isGameTwo) {
            Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameThree) {
            Gdx.gl.glClearColor(GameThreeConstants.BACKGROUND_COLOR.r, GameThreeConstants.BACKGROUND_COLOR.g, GameThreeConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameFour){
            Gdx.gl.glClearColor(GameFourConstants.BACKGROUND_COLOR.r, GameFourConstants.BACKGROUND_COLOR.g, GameFourConstants.BACKGROUND_COLOR.b, 1);
            batch.begin();
            batch.draw(resizedBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if(isGameFour) {
            renderer.setColor(GameFourConstants.SQUARE_COLOR);
            renderer.rect(0, screenHeight / 3, screenWidth, screenHeight/3);
            renderer.setColor(GameFourConstants.SELECTED_COLOR);
        }

        renderer.rect(screenHeight/36, screenHeight / 3 + screenHeight/36, screenWidth - screenHeight/18, screenHeight/3 -  screenHeight/18);
        renderer.end();

        batch.begin();
        font.getData().setScale(.7f);
        if(isGameOne){
            font.setColor(GameOneConstants.TITLE_COLOR);
        } else if(isGameTwo) {
            font.setColor(GameTwoConstants.OUTLINE_COLOR);
        } else if(isGameThree) {
            font.setColor(GameThreeConstants.TITLE_COLOR);
        } else if(isGameFour){
            //font.setColor(GameFourConstants.TITLE_COLOR);
            font.setColor(Color.WHITE);
        }

        final GlyphLayout scoreLayout = new GlyphLayout(font, "Your score is " + Integer.toString(score) + "/" + Integer.toString(trial));
        font.draw(batch, scoreLayout, (screenWidth - scoreLayout.width)/2,
                screenHeight * 7 / 12);

        final GlyphLayout promptLayout_two = new GlyphLayout(font, GameOneConstants.END_INSTRUCTIONS_TWO);
        font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                screenHeight * 5 / 12);

        final GlyphLayout promptLayout_one = new GlyphLayout(font, GameOneConstants.END_INSTRUCTIONS_ONE);
        font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                screenHeight * 5/ 12 + 1.5f * promptLayout_two.height);

        batch.end();

        /*if(exit){
            Gdx.app.exit();
        }*/

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
        if(isGameOne){
            if(gameIndep.actionResolver.getCounter() == 3 || gameIndep.actionResolver.getPatient()){
                Gdx.app.exit();
            } else {
                gameIndep.actionResolver.NextGame();
                Gdx.app.exit();
            }
        } else if(isGameTwo){
            if(gameDep.actionResolver.getCounter() == 3 || gameDep.actionResolver.getPatient()){
                Gdx.app.exit();
            } else {
                gameDep.actionResolver.NextGame();
                Gdx.app.exit();
            }
        } else if(isGameThree){
            if(gameOb.actionResolver.getCounter() == 3 || gameOb.actionResolver.getPatient()){
                Gdx.app.exit();
            } else {
                gameOb.actionResolver.NextGame();
                Gdx.app.exit();
            }
        } else if(isGameFour){
            if(gameSpa.actionResolver.getCounter() == 3 || gameSpa.actionResolver.getPatient()){
                Gdx.app.exit();
            } else {
                gameSpa.actionResolver.NextGame();
                Gdx.app.exit();
            }
        }
        return false;
    }

}
