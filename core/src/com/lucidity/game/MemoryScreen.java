package com.lucidity.game;

/**
 * Created by lixiaoyan on 6/29/18.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.sun.tools.internal.jxc.ap.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.rmi.runtime.Log;

import static com.lucidity.game.GameOneConstants.BACKGROUND_COLOR;

public class MemoryScreen extends InputAdapter implements Screen {
    public static final String TAG = MemoryScreen.class.getName();
    WorkingMemoryGame game;
    GameOneConstants.Difficulty difficulty;

    ExtendViewport memoryViewport;
    ScreenViewport hudViewport;

    ShapeRenderer renderer;

    int screenWidth;
    int screenHeight;

    int score;
    int trial;
    int attemps;

    Rectangle[][] grid;
    int[][] selected;
    int[][] toRemember;
    /*int numOfBlocks;*/
    int blocksHorizontal;
    int blocksVertical;

    Rectangle btnSubmit;
    boolean onSubmit = false;
    boolean correct = false;
    boolean suppressed = false;

    Rectangle btnEnd;
    boolean onEnd = false;

    private SpriteBatch batch;
    public BitmapFont font;

    //to store reaction time of each trial
    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    private float elapsed;
    private boolean disableTouchDown=true;
    //cheap temporary fix

    public MemoryScreen(WorkingMemoryGame game, GameOneConstants.Difficulty difficulty, int points, int trials) {
        this.game = game;

        this.difficulty = difficulty;
        if(this.difficulty.label.equals("Easy")){
            blocksVertical = 1;
            blocksHorizontal = 2;
        } else if(this.difficulty.label.equals("Medium")){
            blocksHorizontal = 2;
            blocksVertical = 2;
        } else {
            blocksHorizontal = 3;
            blocksVertical = 3;
        }

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];

        //changed for portrait
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        grid = new Rectangle[blocksHorizontal][blocksVertical];
        selected = new int[blocksHorizontal][blocksVertical];
        toRemember = new int[blocksHorizontal][blocksVertical];

        for (int i = 0; i < blocksHorizontal; i++) {
            for (int j = 0; j < blocksVertical; j++) {
                Rectangle block = new Rectangle();
                block.width = (screenWidth * 5 / 6) / blocksHorizontal;
                block.height = block.width;
                block.x = i * block.width + screenWidth / 12;
                block.y = j * block.height + screenHeight * 5 / 12;
                grid[i][j] = block;
            }
        }

        this.score = points;
        this.trial = trials;


        btnSubmit = new Rectangle();
        btnSubmit.width = screenWidth / 3;
        btnSubmit.height = screenHeight / 12;
        btnSubmit.x = screenWidth * 13 / 24;
        btnSubmit.y = screenHeight / 6;

        btnEnd = new Rectangle();
        btnEnd.width = screenWidth / 3;
        btnEnd.height = screenHeight / 12;
        btnEnd.x = screenWidth / 8;
        btnEnd.y = screenHeight / 6;


        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);

        generateTrial(this.difficulty);

    }

    @Override
    public void show() {
        memoryViewport = new ExtendViewport(GameOneConstants.WORLD_SIZE, GameOneConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

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


        elapsed += delta;

        if(elapsed > 0 && elapsed < 2) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.setColor(Color.valueOf("#026670"));
            font.getData().setScale(GameOneConstants.NOTIFICATION_SCALE);

            final GlyphLayout promptLayout_two = new GlyphLayout(font, GameOneConstants.PROMPT_TWO);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                    screenHeight / 2);

            final GlyphLayout promptLayout_one = new GlyphLayout(font, GameOneConstants.PROMPT_ONE);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                    screenHeight / 2 + 1.5f * promptLayout_two.height);




            batch.end();
        } else if (elapsed > 4 && elapsed <6) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.getData().setScale(3f);
            font.setColor(Color.valueOf("#026670"));

            final GlyphLayout promptLayout_two = new GlyphLayout(font, GameOneConstants.WAIT_TWO);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                    screenHeight / 2);

            final GlyphLayout promptLayout_one = new GlyphLayout(font, GameOneConstants.WAIT_ONE);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                    screenHeight / 2 + 1.5f * promptLayout_two.height);
            batch.end();

        } else {
            //Start timer
            if (elapsed > 6 && timerStart){
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }

            Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            //This draws the grid in its two conditions:
            //selected or not-selected
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
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
                game.setScreen(new EndScreen(game, score, trial));

            } else {
                renderer.setColor(Color.valueOf("#026670"));
            }
            renderer.rect(btnEnd.x, btnEnd.y, btnEnd.getWidth(), btnEnd.getHeight());
            renderer.end();


            //draw the outline of the blocks
            renderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
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
            font.getData().setScale(GameOneConstants.LABEL_SCALE);
            //font.setColor(Color.valueOf("#9FEDD7"));
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);


            //prints text on submit button
            font.draw(batch, "Submit",
                    (int) (btnSubmit.x + 0.25 * btnSubmit.getWidth()),
                    (int) (btnSubmit.y + 0.6 * btnSubmit.getHeight()));




            //prints text on end button
            font.draw(batch, "End",
                    (int) (btnEnd.x + 0.4 * btnEnd.getWidth()),
                    (int) (btnEnd.y + 0.6 * btnEnd.getHeight()));

            font.setColor(0.0f, 0.0f, 0.0f, 1.0f);

            //prints the correct/incorrect message when the person clicks submit
            //TODO: FIGURE OUT THE CORRECT WAY TO DO THIS PREFERABLY USING PAUSE
            if(suppressed){
                final GlyphLayout promptLayout = new GlyphLayout(font, GameOneConstants.CORRECT_MESSAGE);
                font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
            }
            if (correct && onSubmit) {
                correct = false;
                score++;

                suppressed = true;
                disableTouchDown = true;
                if(trial == 5){
                    postScore();
                    game.setScreen(new EndScreen(game, score, trial));
                }
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       trial++;
                                       correct=false;
                                       suppressed=false;
                                       generateTrial(difficulty);
                                   }
                               },
                        1);
            } else if (!correct && onSubmit && !suppressed) {
                selected = new int[blocksHorizontal][blocksVertical];
                final GlyphLayout promptLayout = new GlyphLayout(font, GameOneConstants.INCORRECT_MESSAGE);
                font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
            }


            //prints the score on the screen of game
            font.draw(batch, GameOneConstants.SCORE_LABEL + Integer.toString(score),
                    GameOneConstants.SCORE_CENTER, screenHeight - GameOneConstants.SCORE_CENTER);

            font.draw(batch, GameOneConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameOneConstants.TRIAL_CENTER, screenHeight - GameOneConstants.SCORE_CENTER);


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
        font.dispose();
        batch.dispose();
        renderer.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //TODO: make sure this actually disables touch
        if(!disableTouchDown) {
            onEnd = btnEnd.contains(screenX, screenHeight - screenY);

            if (btnSubmit.contains(screenX, screenHeight - screenY)) {
                onSubmit = true;
                if (Arrays.deepEquals(selected, toRemember)) {
                    //Save time in seconds
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                        trialSuccess[trial - 1] = attemps + 1;
                        System.out.println(trialTime[trial - 1]);
                    }
                    correct = true;
                } else {
                    attemps++;
                    if (attemps >= 3) {
                        //Save time in seconds
                        if (trial <= 5) {
                            trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                            trialSuccess[trial - 1] = attemps + 1;
                            System.out.println(trialTime[trial - 1]);
                        }

                        disableTouchDown = true;
                        if(trial == 5){
                            postScore();
                            game.setScreen(new EndScreen(game, score, trial));
                        }
                        Timer.schedule(new Timer.Task() {
                                           @Override
                                           public void run() {
                                               trial++;
                                               correct=false;
                                               suppressed=false;
                                               generateTrial(difficulty);
                                           }
                                       },
                                1);
                    }
                }
            } else {
                for (int i = 0; i < blocksHorizontal; i++) {
                    for (int j = 0; j < blocksVertical; j++) {
                        if (grid[i][j].contains(screenX, screenHeight - screenY)) {
                            selected[i][j] = 1 - selected[i][j];
                        }
                    }
                }
            }
        }

        return true;
    }

    private void generateTrial(GameOneConstants.Difficulty difficulty){
        int blocks;
        int predicted = 0;
        elapsed = 0;
        timerStart = true;
        toRemember = new int[blocksHorizontal][blocksVertical];
        attemps = 0;
        if(this.difficulty.label.equals("Easy")){
            blocks = 1;
        } else if(this.difficulty.label.equals("Medium")){
            blocks = 2;
        } else {
            blocks = 4;
        }

        while(predicted < blocks){
            int on = (int)(Math.random()*blocksHorizontal*blocksVertical);

            if(toRemember[on/blocksVertical%blocksHorizontal][on%blocksVertical] != 1){
                ++predicted;
                toRemember[on/blocksVertical%blocksHorizontal][on%blocksVertical] = 1;
            }
        }


        //flashes randomly generated pattern for a certain period of time
        selected = toRemember;
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               selected = new int[blocksHorizontal][blocksVertical];
                           }
                       },
                4);
        //30 / 30.0f
    }

    //Posts score and stats to MySQL database
    private void postScore(){
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_blockgame_score.php");

        //set parameters
        Map<String, String> json = new HashMap<String, String>();
        json.put("username", game.getUsername());
        json.put("difficulty", difficulty.label);
        json.put("score", String.valueOf(score));
        for (int i = 0; i < trial; i++) {
            String trialNum = "trial" + (i+1);
            json.put(trialNum, String.valueOf(trialSuccess[i]));
            json.put(trialNum + "time", String.valueOf(trialTime[i]));
        }

        httpPost.setContent(HttpParametersUtils.convertHttpParameters(json));

        //Send JSON and Look for response
        Gdx.net.sendHttpRequest (httpPost, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String status = httpResponse.getResultAsString().trim();
                HashMap<String,String> map = new Gson().fromJson(status, new TypeToken<HashMap<String, String>>(){}.getType());
                System.out.println(map);
            }

            public void failed(Throwable t) {
                String status = "failed";
            }

            @Override
            public void cancelled() {

            }
        });
    }
}