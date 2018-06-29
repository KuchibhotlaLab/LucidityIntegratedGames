package com.lucidity.game;

/**
 * Created by lixiaoyan on 6/29/18.
 */
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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import com.sun.tools.internal.jxc.ap.Const;

import java.util.Arrays;
import java.util.Random;

import sun.rmi.runtime.Log;

import static com.lucidity.game.Constants.BACKGROUND_COLOR;

public class MemoryScreen extends InputAdapter implements Screen {
    public static final String TAG = MemoryScreen.class.getName();
    MemoryGame game;
    Constants.Difficulty difficulty;

    ExtendViewport memoryViewport;
    ScreenViewport hudViewport;

    ShapeRenderer renderer;

    int screenWidth;
    int screenHeight;

    int score;
    int trial;

    Rectangle[][] grid;
    int[][] selected;
    int[][] toRemember;
    int numOfBlocks;

    Rectangle btnSubmit;
    boolean onSubmit = false;
    boolean correct = false;

    Rectangle btnEnd;
    boolean onEnd = false;

    private SpriteBatch batch;
    public BitmapFont font;



    private float elapsed;
    private float incorrectOffset;

    public MemoryScreen(MemoryGame game, Constants.Difficulty difficulty, int points, int trials) {
        this.game = game;

        this.difficulty = difficulty;
        if(this.difficulty.label.equals("Easy")){
            numOfBlocks = 2;
        } else if(this.difficulty.label.equals("Medium")){
            numOfBlocks = 3;
        } else {
            numOfBlocks = 4;
        }



        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        grid = new Rectangle[numOfBlocks][numOfBlocks];
        selected = new int[numOfBlocks][numOfBlocks];
        toRemember = new int[numOfBlocks][numOfBlocks];


        //for landscape layout
        for (int i = 0; i < numOfBlocks; i++) {
            for (int j = 0; j < numOfBlocks; j++) {
                Rectangle block = new Rectangle();
                block.width = (screenHeight * 2 / 3) / numOfBlocks;
                block.height = block.width;
                block.x = i * block.width + screenHeight / 3;
                block.y = j * block.height + screenHeight / 6;
                grid[i][j] = block;
                toRemember[i][j] = (int) (Math.random() * 2);
            }
        }

        this.score = points;
        this.trial = trials;

        btnSubmit = new Rectangle();
        btnSubmit.width = screenWidth / 6;
        btnSubmit.height = screenHeight / 3 - screenHeight / 24;
        btnSubmit.x = screenWidth * 3 / 4;
        btnSubmit.y = screenHeight / 2 + screenHeight / 24;

        btnEnd = new Rectangle();
        btnEnd.width = screenWidth / 6;
        btnEnd.height = screenHeight / 3 - screenHeight / 24;
        btnEnd.x = screenWidth * 3 / 4;
        btnEnd.y = screenHeight / 6;


        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void show() {
        memoryViewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);


        //flashes randomly generated pattern for a certain period of time
        selected = toRemember;
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               selected = new int[numOfBlocks][numOfBlocks];
                           }
                       },
                2);
        //30 / 30.0f


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
    }

    @Override
    public void render(float delta) {

        memoryViewport.apply(true);


        elapsed += delta;

        if(elapsed > 0 && elapsed < 1) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.setColor(Color.valueOf("#9FEDD7"));
            font.getData().setScale(5f);
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);

            font.draw(batch, "Remember the position of light colored blocks",screenWidth/8, screenHeight/2);
            batch.end();
        } else if (elapsed > 2.25 && elapsed <3.25) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.setColor(Color.valueOf("#9FEDD7"));
            font.getData().setScale(5f);
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);

            font.draw(batch, "This is the blank screen",screenWidth/4, screenHeight/2);
            batch.end();

        } else {
            Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            //This draws the grid in its two conditions:
            //selected or not-selected
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < numOfBlocks; i++) {
                for (int j = 0; j < numOfBlocks; j++) {
                    if (selected[i][j] == 1) {
                        renderer.setColor(Color.valueOf("#9FEDD7"));
                    } else {
                        renderer.setColor(Color.valueOf("#026670"));
                    }
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }

            //change the color of the submit button when it is pressed
            if (onSubmit) {
                renderer.setColor(Color.valueOf("#9FEDD7"));

                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {onSubmit = false;
                                   }
                               },
                        30/30.0f);

                //offset for displaying "incorrect"
            } else {
                renderer.setColor(Color.valueOf("#026670"));
            }
            renderer.rect(btnSubmit.x, btnSubmit.y, btnSubmit.getWidth(), btnSubmit.getHeight());


            if (onEnd) {
                //END THE GAME
                renderer.setColor(Color.valueOf("#9FEDD7"));
                game.dispose();
                game.setScreen(new DifficultyScreen(game));
                //TODO: properly dispose screen

            } else {
                renderer.setColor(Color.valueOf("#026670"));
            }
            renderer.rect(btnEnd.x, btnEnd.y, btnEnd.getWidth(), btnEnd.getHeight());
            renderer.end();


            //draw the outline of the blocks
            renderer.begin(ShapeRenderer.ShapeType.Line);

            for (int i = 0; i < numOfBlocks; i++) {
                for (int j = 0; j < numOfBlocks; j++) {
                    if (selected[i][j] == 1) {
                        renderer.setColor(Color.valueOf("#026670"));
                    } else {
                        renderer.setColor(Color.valueOf("#9FEDD7"));
                    }
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();


            //draws all the texts (submit and correct/incorrect message)
            batch.begin();
            font.getData().setScale(5f);
            font.setColor(Color.valueOf("#9FEDD7"));


            //prints text on submit button
            font.draw(batch, "Submit",
                    (int) (btnSubmit.x + 0.15 * btnSubmit.getWidth()),
                    (int) (btnSubmit.y + 0.6 * btnSubmit.getHeight()));


            //prints text on end button
            font.draw(batch, "End",
                    (int) (btnEnd.x + 0.3 * btnEnd.getWidth()),
                    (int) (btnEnd.y + 0.6 * btnEnd.getHeight()));


            //prints the correct/incorrect message when the person clicks submit
            if (correct && onSubmit) {
                font.draw(batch, "You are correct", screenWidth /4, screenHeight / 10);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       correct = false;
                                       score++;
                                       trial++;
                                       game.setScreen(new MemoryScreen(game, difficulty, score, trial));
                                   }
                               },
                        15/30.0f);
            } else if (!correct && onSubmit) {
                //TODO: fix printing incorrect here
                selected =new int[numOfBlocks][numOfBlocks];
                font.draw(batch, "You are Incorrect", screenWidth/4, screenHeight / 10);
            }


            //prints the score on the screen of game
            font.draw(batch, Constants.SCORE_LABEL + Integer.toString(score),
                    Constants.SCORE_CENTER, screenHeight - Constants.SCORE_CENTER);

            font.draw(batch, Constants.TRIAL_LABEL + Integer.toString(trial),
                    Constants.TRIAL_CENTER, screenHeight - Constants.SCORE_CENTER);


            batch.end();
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


    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //landscape
        onEnd = btnEnd.contains(screenX, screenHeight - screenY);

        if (btnSubmit.contains(screenX, screenHeight - screenY)) {
            onSubmit = true;
            if (Arrays.deepEquals(selected, toRemember)) {
                correct = true;
            }
        } else {
            for (int i = 0; i < numOfBlocks; i++) {
                for (int j = 0; j < numOfBlocks; j++) {
                    if (grid[i][j].contains(screenX, screenHeight - screenY)) {
                        selected[i][j] = 1 - selected[i][j];
                    }
                }
            }
        }
        return true;
    }
}