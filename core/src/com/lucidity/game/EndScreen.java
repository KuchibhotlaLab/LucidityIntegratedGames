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
    RecallGame gameRec;

    ExtendViewport memoryViewport;
    ScreenViewport hudViewport;
    ShapeRenderer renderer;

    boolean isGameOne, isGameTwo, isGameThree, isGameFour, isGameFive = false;


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

        memoryViewport = new ExtendViewport(BlockGameConstants.WORLD_SIZE, BlockGameConstants.WORLD_SIZE);
    }


    public EndScreen(FacialMemoryGame game, int points, int trials) {
        this.gameDep = game;
        this.score = points;
        this.trial = trials;
        isGameTwo = true;

        memoryViewport = new ExtendViewport(FacialGameConstants.WORLD_SIZE, FacialGameConstants.WORLD_SIZE);
    }

    public EndScreen(ObjectRecognitionGame game, int points, int trials) {
        this.gameOb = game;
        this.score = points;
        this.trial = trials;
        isGameThree = true;

        memoryViewport = new ExtendViewport(ObjectGameConstants.WORLD_SIZE, ObjectGameConstants.WORLD_SIZE);
    }

    public EndScreen(SpatialMemoryGame game, int points, int trials) {
        this.gameSpa = game;
        this.score = points;
        this.trial = trials;
        isGameFour = true;

        memoryViewport = new ExtendViewport(SpatialGameConstants.WORLD_SIZE, SpatialGameConstants.WORLD_SIZE);

    }

    public EndScreen(RecallGame game, int points, int trials) {
        this.gameRec = game;
        this.score = points;
        this.trial = trials;
        isGameFive = true;

        memoryViewport = new ExtendViewport(SpatialGameConstants.WORLD_SIZE, SpatialGameConstants.WORLD_SIZE);

    }



    @Override
    public void show() {

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        hudViewport = new ScreenViewport();

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
            Gdx.gl.glClearColor(BlockGameConstants.BACKGROUND_COLOR.r,BlockGameConstants.BACKGROUND_COLOR.g,BlockGameConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameTwo) {
            Gdx.gl.glClearColor(FacialGameConstants.BACKGROUND_COLOR.r, FacialGameConstants.BACKGROUND_COLOR.g, FacialGameConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameThree) {
            Gdx.gl.glClearColor(ObjectGameConstants.BACKGROUND_COLOR.r, ObjectGameConstants.BACKGROUND_COLOR.g, ObjectGameConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameFour){
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.rect(
                    0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    SpatialGameConstants.BACKGROUND_COLOR_BOT, SpatialGameConstants.BACKGROUND_COLOR_BOT,
                    SpatialGameConstants.BACKGROUND_COLOR_TOP, SpatialGameConstants.BACKGROUND_COLOR_TOP
            );
            renderer.end();
            //Gdx.gl.glClearColor(SpatialGameConstants.BACKGROUND_COLOR.r, SpatialGameConstants.BACKGROUND_COLOR.g, SpatialGameConstants.BACKGROUND_COLOR.b, 1);
        } else if(isGameFive) {
            Gdx.gl.glClearColor(RecallGameConstants.BACKGROUND_COLOR.r, RecallGameConstants.BACKGROUND_COLOR.g, RecallGameConstants.BACKGROUND_COLOR.b, 1);
        }

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.rect(screenHeight/36, screenHeight / 3 + screenHeight/36, screenWidth - screenHeight/18, screenHeight/3 -  screenHeight/18);
        renderer.end();

        batch.begin();
        font.getData().setScale(.7f);
        if(isGameOne){
            font.setColor(BlockGameConstants.TITLE_COLOR);
        } else if(isGameTwo) {
            font.setColor(FacialGameConstants.OUTLINE_COLOR);
        } else if(isGameThree) {
            font.setColor(ObjectGameConstants.TITLE_COLOR);
        } else if(isGameFour){
            font.setColor(SpatialGameConstants.TITLE_COLOR);
            //font.setColor(Color.WHITE);
        } else if(isGameFive) {
            font.setColor(RecallGameConstants.TITLE_COLOR);
        }

        final GlyphLayout scoreLayout = new GlyphLayout(font, "Your score is " + Integer.toString(score) + "/" + Integer.toString(trial));
        font.draw(batch, scoreLayout, (screenWidth - scoreLayout.width)/2,
                screenHeight * 7 / 12);

        final GlyphLayout promptLayout_two = new GlyphLayout(font, BlockGameConstants.END_INSTRUCTIONS_TWO);
        font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                screenHeight * 5 / 12);

        final GlyphLayout promptLayout_one = new GlyphLayout(font, BlockGameConstants.END_INSTRUCTIONS_ONE);
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
                gameIndep.scorePoster.postSuiteOnline(gameIndep.getUsername());
                Gdx.app.exit();
            } else {
                gameIndep.actionResolver.NextGame(gameIndep.scorePoster.getTestSuiteStartTime());
                Gdx.app.exit();
            }
        } else if(isGameTwo){
            if(gameDep.actionResolver.getCounter() == 3 || gameDep.actionResolver.getPatient()){
                gameDep.scorePoster.postSuiteOnline(gameDep.getUsername());
                Gdx.app.exit();
            } else {
                gameDep.actionResolver.NextGame(gameDep.scorePoster.getTestSuiteStartTime());
                Gdx.app.exit();
            }
        } else if(isGameThree){
            if(gameOb.actionResolver.getCounter() == 3 || gameOb.actionResolver.getPatient()){
                gameOb.scorePoster.postSuiteOnline(gameOb.getUsername());
                Gdx.app.exit();
            } else {
                gameOb.actionResolver.NextGame(gameOb.scorePoster.getTestSuiteStartTime());
                Gdx.app.exit();
            }
        } else if(isGameFour){
            if(gameSpa.actionResolver.getCounter() == 3 || gameSpa.actionResolver.getPatient()){
                gameSpa.scorePoster.postSuiteOnline(gameSpa.getUsername());
                Gdx.app.exit();
            } else {
                gameSpa.actionResolver.NextGame(gameSpa.scorePoster.getTestSuiteStartTime());
                Gdx.app.exit();
            }
        } else if(isGameFive){
            if(gameRec.actionResolver.getCounter() == 3 || gameRec.actionResolver.getPatient()){
                gameRec.scorePoster.postSuiteOnline(gameRec.getUsername());
                Gdx.app.exit();
            } else {
                gameRec.actionResolver.NextGame(gameRec.scorePoster.getTestSuiteStartTime());
                Gdx.app.exit();
            }
        }
        return false;
    }

}
