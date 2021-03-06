package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.sun.tools.internal.jxc.ap.Const;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import sun.rmi.runtime.Log;

import static com.lucidity.game.BlockGameConstants.BACKGROUND_COLOR;

public class MemoryScreen extends InputAdapter implements Screen {
    public static final String TAG = MemoryScreen.class.getName();
    private WorkingMemoryGame game;
    int difficulty;

    private ExtendViewport memoryViewport;
    private ScreenViewport hudViewport;

    private ShapeRenderer renderer;

    private Texture backdrop;

    private int screenWidth, screenHeight;

    private int score, trial, attemps;

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
    private boolean pauseTimerEnd;
    private long attemptStartTime;
    private long pauseStartTime;
    private int[] trialSuccess;
    private double[][] attemptTime;

    private float elapsed;
    private boolean disableTouchDown=true;
    //cheap temporary fix


    public MemoryScreen(WorkingMemoryGame game, int diff, int points, int trials) {
        this.game = game;

        this.difficulty = diff;
        if(this.difficulty == BlockGameConstants.DIFFICULTY_EASY){
            blocksVertical = 1;
            blocksHorizontal = 2;
        } else if(this.difficulty == BlockGameConstants.DIFFICULTY_MEDIUM){
            blocksHorizontal = 2;
            blocksVertical = 2;
        } else {
            blocksHorizontal = 3;
            blocksVertical = 3;
        }

        timerStart = true;
        attemptTime = new double[5][3];
        trialSuccess = new int[5];

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        backdrop = new Texture(Gdx.files.internal("data/memoryBtn.png"));

        grid = new Rectangle[blocksHorizontal][blocksVertical];
        selected = new int[blocksHorizontal][blocksVertical];
        toRemember = new int[blocksHorizontal][blocksVertical];
        int offset = screenWidth / 40;
        int width = (screenWidth * 5 / 6) / blocksHorizontal;
        int startX = (screenWidth - offset * (blocksHorizontal - 1) - width * (blocksHorizontal - 1)) * 2 / 13;


        for (int i = 0; i < blocksHorizontal; i++) {
            for (int j = 0; j < blocksVertical; j++) {
                Rectangle block = new Rectangle();
                block.width = width;
                block.height = block.width;
                if((i == 0 && j == 0) || (i == blocksHorizontal - 1 && j == blocksVertical - 1)){
                    block.x = i * block.width + screenWidth / 12;
                    block.y = j * block.height + screenHeight  / 4;
                }
                block.x = i * block.width + startX + offset * i;
                block.y = j * block.height + screenHeight  / 4 + offset * j;
                grid[i][j] = block;
            }
        }
        this.score = points;
        this.trial = trials;


        btnSubmit = new Rectangle();
        btnSubmit.width = screenWidth / 3;
        btnSubmit.height = screenHeight / 12;
        btnSubmit.x = screenWidth * 13 / 24;
        btnSubmit.y = screenHeight / 8;

        btnEnd = new Rectangle();
        btnEnd.width = screenWidth / 3;
        btnEnd.height = screenHeight / 12;
        btnEnd.x = screenWidth / 8;
        btnEnd.y = screenHeight / 8;


        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);

        generateTrial(this.difficulty);

    }

    @Override
    public void show() {
        memoryViewport = new ExtendViewport(BlockGameConstants.WORLD_SIZE, BlockGameConstants.WORLD_SIZE);
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
        Gdx.gl.glClearColor(BlockGameConstants.BACKGROUND_COLOR.r, BlockGameConstants.BACKGROUND_COLOR.g, BlockGameConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBackground();
        renderer.end();
        elapsed += delta;

        if(elapsed > 0 && elapsed < 2) {
            //Gdx.gl.glClearColor(1, 1, 1, 1);
            //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.setColor(BlockGameConstants.TITLE_COLOR);
            font.getData().setScale(BlockGameConstants.NOTIFICATION_SCALE);

            if(trial == 1) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, BlockGameConstants.PROMPT_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2,
                        screenHeight / 2);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, BlockGameConstants.PROMPT_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight / 2 + 1.5f * promptLayout_two.height);
            } else {
                final GlyphLayout promptLayout_next = new GlyphLayout(font, BlockGameConstants.PROMPT_NEXT);
                font.draw(batch, promptLayout_next, (screenWidth - promptLayout_next.width) / 2,
                        screenHeight / 2);
            }

            batch.end();
        } else if (elapsed > 4 && elapsed <6) {
            //Gdx.gl.glClearColor(BlockGameConstants.BACKGROUND_COLOR.r, BlockGameConstants.BACKGROUND_COLOR.g, BlockGameConstants.BACKGROUND_COLOR.b, 1);
            //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.getData().setScale(.6f);
            font.setColor(BlockGameConstants.TITLE_COLOR);

            final GlyphLayout promptLayout_two = new GlyphLayout(font, BlockGameConstants.WAIT_TWO);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                    screenHeight / 2);

            final GlyphLayout promptLayout_one = new GlyphLayout(font, BlockGameConstants.WAIT_ONE);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                    screenHeight / 2 + 1.5f * promptLayout_two.height);
            batch.end();

        } else {
            if (TimeUtils.nanoTime() - pauseStartTime > 1000000000.0 && pauseTimerEnd) {
                timerStart = true;
                pauseTimerEnd = false;
            }

            //Start timer
            if (elapsed > 6 && timerStart){
                attemptStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }

            //Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, 1);
            //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            //This draws the grid in its two conditions:
            //selected or not-selected
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    if (selected[i][j] == 1) {
                        renderer.setColor(BlockGameConstants.SELECTED_COLOR);
                    } else {
                        renderer.setColor(BlockGameConstants.NOT_SELECTED_COLOR);
                    }
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();


            if (elapsed > 6) {
                //change the color of the submit button when it is pressed
                /*if (onSubmit) {
                    renderer.setColor(Color.valueOf("#9FEDD7"));

                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           onSubmit = false;
                                       }
                                   },
                            30 / 30.0f);

                    //offset for displaying "incorrect"
                } else {
                    renderer.setColor(Color.valueOf("#026670"));
                }
                renderer.rect(btnSubmit.x, btnSubmit.y, btnSubmit.getWidth(), btnSubmit.getHeight());*/
                batch.begin();
                if (onSubmit) {
                    backdrop = new Texture(Gdx.files.internal("data/memoryBtnPressed.png"));

                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           onSubmit = false;
                                       }
                                   },
                            30 / 30.0f);
                } else {
                    backdrop = new Texture(Gdx.files.internal("data/memoryBtn.png"));
                }
                batch.draw(backdrop, btnSubmit.x, btnSubmit.y, btnSubmit.width, btnSubmit.height);

                if (onEnd) {
                    //END THE GAME
                    backdrop = new Texture(Gdx.files.internal("data/memoryBtnPressed.png"));
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           //onSubmit = false;
                                           game.setScreen(new EndScreen(game, score, trial));
                                       }
                                   },
                            30 / 30.0f);
                } else {
                    backdrop = new Texture(Gdx.files.internal("data/memoryBtn.png"));
                }
                batch.draw(backdrop, btnEnd.x, btnEnd.y, btnEnd.width, btnEnd.height);
                batch.end();

                /*renderer.begin(ShapeRenderer.ShapeType.Filled);
                if (onEnd) {
                    //END THE GAME
                    renderer.setColor(Color.valueOf("#9FEDD7"));
                    game.setScreen(new EndScreen(game, score, trial));

                } else {
                    renderer.setColor(Color.valueOf("#026670"));
                }
                renderer.rect(btnEnd.x, btnEnd.y, btnEnd.getWidth(), btnEnd.getHeight());
                renderer.end();*/
            }


            //draws all the texts (submit and correct/incorrect message)
            batch.begin();
            font.getData().setScale(BlockGameConstants.LABEL_SCALE);
            //font.setColor(Color.valueOf("#9FEDD7"));
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);


            if (elapsed > 6) {
                //prints text on submit button
                if(onSubmit) {
                    font.setColor(BlockGameConstants.NOT_SELECTED_COLOR);
                } else {
                    font.setColor(BlockGameConstants.SELECTED_COLOR);
                }
                font.draw(batch, "Submit",
                        (int) (btnSubmit.x + 0.25 * btnSubmit.getWidth()),
                        (int) (btnSubmit.y + 0.6 * btnSubmit.getHeight()));

                if(onEnd) {
                    font.setColor(BlockGameConstants.NOT_SELECTED_COLOR);
                } else {
                    font.setColor(BlockGameConstants.SELECTED_COLOR);
                }
                //prints text on end button
                font.draw(batch, "End",
                        (int) (btnEnd.x + 0.4 * btnEnd.getWidth()),
                        (int) (btnEnd.y + 0.6 * btnEnd.getHeight()));
            }

            //prints the correct/incorrect message when the person clicks submit
            if(suppressed){
                font.setColor(BlockGameConstants.CORRECT_COLOR);
                final GlyphLayout promptLayout = new GlyphLayout(font, BlockGameConstants.CORRECT_MESSAGE);
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
                font.setColor(BlockGameConstants.INCORRECT_COLOR);
                final GlyphLayout promptLayout = new GlyphLayout(font, BlockGameConstants.INCORRECT_MESSAGE);
                font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
            }

            font.getData().setScale(BlockGameConstants.ANSWER_SCALE);
            font.setColor(BlockGameConstants.TITLE_COLOR);

            //batch.draw(backdrop, BlockGameConstants.SCORE_CENTER, screenHeight - BlockGameConstants.SCORE_CENTER - screenHeight/24, screenWidth / 5, screenHeight/18);
            //batch.draw(backdrop, BlockGameConstants.TRIAL_CENTER, screenHeight - BlockGameConstants.SCORE_CENTER  - screenHeight/24,  screenWidth/7, screenHeight/18);

            //prints the score on the screen of game
            font.draw(batch, BlockGameConstants.SCORE_LABEL + Integer.toString(score),
                    BlockGameConstants.SCORE_CENTER, screenHeight - BlockGameConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, FacialGameConstants.SCORE_LABEL);

            font.draw(batch, BlockGameConstants.TRIAL_LABEL + Integer.toString(trial),
                    BlockGameConstants.TRIAL_CENTER,
                    screenHeight - BlockGameConstants.SCORE_CENTER - layout_scores.height * 1.5f);


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

        if(!disableTouchDown) {
            onEnd = btnEnd.contains(screenX, screenHeight - screenY);

            if (btnSubmit.contains(screenX, screenHeight - screenY)) {
                onSubmit = true;

                //Save time in seconds
                if(trial <= 5) {
                    attemptTime[trial - 1][attemps] = (TimeUtils.nanoTime() - attemptStartTime) / 1000000000.0;
                    trialSuccess[trial - 1] = attemps + 1;
                    System.out.println(attemptTime[trial - 1][attemps]);
                }

                if (Arrays.deepEquals(selected, toRemember)) {
                    correct = true;
                } else {
                    attemps++;
                    disableTouchDown = true;
                    pauseStartTime = TimeUtils.nanoTime();
                    pauseTimerEnd = true;
                    if (attemps >= 3) {
                        trialSuccess[trial - 1] = attemps + 1;
                        //Save time in seconds
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

    private void drawBackground(){
        renderer.setColor(BlockGameConstants.CIRCLE_COLOR);
        int r = screenWidth * 3 / 10;
        renderer.circle(screenWidth * 3 / 10, screenHeight/12, r, r);
        renderer.circle(screenWidth, screenHeight/3, r, r);
        renderer.circle(screenWidth / 10, screenHeight*3/4, r, r);
        renderer.circle(screenWidth * 3 /4, screenHeight * 9/10, r, r);
    }

    private void generateTrial(int difficulty){
        int blocks;
        int predicted = 0;
        elapsed = 0;
        timerStart = true;
        toRemember = new int[blocksHorizontal][blocksVertical];
        attemps = 0;
        if(this.difficulty == BlockGameConstants.DIFFICULTY_EASY){
            blocks = 1;
        } else if(this.difficulty == BlockGameConstants.DIFFICULTY_MEDIUM){
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
        json.put("time", game.getDateTime());
        json.put("location", game.getLocation());
        final String menu;
        if (game.getLucid()) {
            menu = "Lucid";
        } else if (game.getPatient()) {
            menu = "Patient";
        } else {
            menu = "CareGiver";
        }
        json.put("menu", menu);
        final String difficult;
        if (difficulty == 2) {
            difficult = "Hard";
        } else if(difficulty == 1) {
            difficult = "Medium";
        } else{
            difficult = "Easy";
        }
        json.put("difficulty", difficult);
        json.put("score", String.valueOf(score));
        for (int i = 0; i < trial; i++) {
            String trialNum = "trial" + (i+1);
            json.put(trialNum, String.valueOf(trialSuccess[i]));
            for (int j = 0; j < 3; j++) {
                json.put(trialNum + "-" + (j + 1) + "time", String.valueOf(attemptTime[i][j]));
            }
        }

        httpPost.setContent(HttpParametersUtils.convertHttpParameters(json));

        //Send JSON and Look for response
        Gdx.net.sendHttpRequest (httpPost, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    //success
                    String status = httpResponse.getResultAsString().trim();
                    HashMap<String, String> map = new Gson().fromJson(status, new TypeToken<HashMap<String, String>>() {
                    }.getType());
                    System.out.println(map);

                    game.scorePoster.postOnline(game.getUsername());

                } else {
                    // save scores locally
                    game.scorePoster.postScoreBlock(game.getUsername(), game.getDateTime(), game.getLocation(),
                            menu, difficult, score, trialSuccess, attemptTime);
                }
            }

            public void failed(Throwable t) {
                String status = "failed";
                // save scores locally
                game.scorePoster.postScoreBlock(game.getUsername(), game.getDateTime(), game.getLocation(),
                        menu, difficult, score, trialSuccess, attemptTime);
            }

            @Override
            public void cancelled() {

            }
        });

        if(game.getCare() || game.getLucid()) {
            game.scorePoster.updateTestRun(game.getUsername(), game.actionResolver.getCounter(), game.getDateTime());
        }
    }
}