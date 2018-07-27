package com.lucidity.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiaoyan on 7/20/18.
 */

public class SpacialScreen extends InputAdapter implements Screen {
    private SpacialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth, screenHeight;
    int score, trial;

    ShapeRenderer renderer;

    SpriteBatch batch;
    BitmapFont font;

    Rectangle end, back;
    boolean onEnd, onBack = false;

    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    float elapsed = 0;
    float delayed = 0;
    int gameMode;

    Rectangle[][] grid;
    boolean[][] selected;
    boolean[][] toRemember;
    int blocksHorizontal;
    int blocksVertical;

    int roadBlockNum;
    int prevX, prevY;

    Rectangle btnSubmit;
    boolean onSubmit = false;

    int startRow, startCol, endRow, endCol;

    private boolean disableTouchDown = true;
    private boolean delayOn = false;

    public SpacialScreen(SpacialMemoryGame game, int mode) {
        this.game = game;
        score = 0;
        trial = 1;

        gameMode = mode;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];

        roadBlockNum = gameMode + 1;
        blocksHorizontal = gameMode + 2;
        blocksVertical = gameMode + 2;
        grid = new Rectangle[blocksHorizontal][blocksVertical];
        selected = new boolean[blocksHorizontal][blocksVertical];
        toRemember = new boolean[blocksHorizontal][blocksVertical];

        btnSubmit = new Rectangle();
        btnSubmit.width = screenWidth / 2;
        btnSubmit.height = screenHeight / 12;
        btnSubmit.x = screenWidth  / 4;
        btnSubmit.y = screenHeight / 6;

        for (int i = 0; i < blocksHorizontal; i++) {
            for (int j = 0; j < blocksVertical; j++) {
                Rectangle block = new Rectangle();
                block.width = (screenWidth * 5 / 6) / blocksHorizontal;
                block.height = block.width;
                block.x = i * block.width + screenWidth / 12;
                block.y = j * block.height + screenHeight  / 3;
                grid[i][j] = block;
                selected[i][j] = false;
            }
        }

        generateTrial();

    }
    @Override
    public void show() {
        viewport = new ExtendViewport(GameFourConstants.WORLD_SIZE, GameFourConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        viewport.apply(true);
        Gdx.gl.glClearColor(GameFourConstants.BACKGROUND_COLOR.r, GameFourConstants.BACKGROUND_COLOR.g, GameFourConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;

        if(elapsed < 1.5f) {
            disableTouchDown = true;
            batch.begin();
            font.getData().setScale(GameFourConstants.INSTRUCTION_SIZE);
            font.setColor(Color.WHITE);
            final GlyphLayout hardLayout = new GlyphLayout(font, GameFourConstants.INSTRUCTIONS);
            font.draw(batch, hardLayout, (screenWidth - hardLayout.width) / 2, screenHeight  / 7);
            batch.end();

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    selectState(toRemember[i][j]);
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();

            renderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    renderer.setColor(GameFourConstants.HARD_COLOR);
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();


        } else if(elapsed == 1.5f) {
            selected = new boolean[blocksHorizontal][blocksVertical];
        } else if (elapsed < 3f) {

            batch.begin();

            final GlyphLayout layout_four = new GlyphLayout(font, GameFourConstants.INSTRUCTIONS_FOUR);
            font.draw(batch, layout_four, (screenWidth - layout_four.width) / 2, screenHeight / 8);
            final GlyphLayout layout_three = new GlyphLayout(font, GameFourConstants.INSTRUCTIONS_THREE);
            font.draw(batch, layout_three, (screenWidth - layout_three.width) / 2, screenHeight / 8 + 1.5f * layout_four.height);
            final GlyphLayout layout_two = new GlyphLayout(font, GameFourConstants.INSTRUCTIONS_TWO);
            font.draw(batch, layout_two, (screenWidth - layout_two.width) / 2, screenHeight  / 8 + 1.5f * layout_three.height + + 1.5f * layout_four.height);


            batch.end();

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            selected[startRow][startCol] = true;
            selected[endRow][endCol] = true;

            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    selectState(selected[i][j]);
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();

            renderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    renderer.setColor(GameFourConstants.HARD_COLOR);
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();

        } else {
            if (timerStart){
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }
            //This draws the grid in its two conditions:
            //selected or not-selected
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    selectState(selected[i][j]);
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();

            //draw the outline of the blocks
            renderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    renderer.setColor(GameFourConstants.HARD_COLOR);
                    renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                }
            }
            renderer.end();


            if(!onSubmit){
                renderer.setColor(GameFourConstants.TITLE_COLOR);
            } else {
                renderer.setColor(GameFourConstants.EASY_COLOR);
                onSubmit = false;
                delayed = elapsed;
                delayOn = true;
                disableTouchDown = true;
                if(trial <= 5) {
                    trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                }
            }
            if(delayOn){
                renderer.setColor(GameFourConstants.EASY_COLOR);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            selectState(onSubmit);
            renderer.rect(btnSubmit.x, btnSubmit.y, btnSubmit.width, btnSubmit.height);


            if(!onEnd){
                renderer.setColor(GameFourConstants.TITLE_COLOR);
            } else {
                disableTouchDown = true;
                renderer.setColor(GameFourConstants.EASY_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       game.setScreen(new EndScreen(game, score, trial));
                                   }
                               },
                        1);
            }

            renderer.rect(end.x, end.y, end.width, end.height);


            if(!onBack){
                renderer.setColor(GameFourConstants.TITLE_COLOR);
            } else {
                disableTouchDown = true;
                renderer.setColor(GameFourConstants.EASY_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {game.setScreen(new DifficultyScreen(game));
                                   }
                               },
                        1);
            }
            renderer.rect(back.x, back.y, back.width, back.height);
            renderer.end();

            batch.begin();
            font.getData().setScale(GameThreeConstants.ANSWER_SCALE);
            font.setColor(Color.WHITE);
            //prints text on back button
            font.draw(batch, GameTwoConstants.BACK_TEXT,
                    (int) (back.x + 0.2 * back.getWidth()),
                    (int) (back.y + 0.6 * back.getHeight()));
            //prints text on end button
            font.draw(batch, GameTwoConstants.END_TEXT,
                    (int) (end.x + 0.25 * end.getWidth()),
                    (int) (end.y + 0.6 * end.getHeight()));

            font.setColor(Color.WHITE);
            font.draw(batch, GameTwoConstants.SCORE_LABEL + Integer.toString(score),
                    GameTwoConstants.SCORE_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, GameTwoConstants.SCORE_LABEL);

            font.draw(batch, GameTwoConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameTwoConstants.SCORE_CENTER,
                    screenHeight - GameTwoConstants.SCORE_CENTER - layout_scores.height * 1.5f);


            final GlyphLayout layout_one = new GlyphLayout(font, GameFourConstants.SUBMIT_TEXT);
            final float fontX_one = (screenWidth - layout_one.width) / 2;
            final float fontY_one = (btnSubmit.height * 0.6f + btnSubmit.y);
            font.draw(batch, layout_one, fontX_one, fontY_one);


            if (delayOn) {
                if (correctPath()) {
                    font.setColor(GameOneConstants.CORRECT_COLOR);
                    final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                    font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 9);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight  / 9 + 1.5f * reactionLayout.height);

                } else {
                    font.setColor(GameOneConstants.INCORRECT_COLOR);
                    final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                    font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 9);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight  / 9 + 1.5f * reactionLayout.height);
                }
            }


            batch.end();

            if(elapsed - delayed >= 1f && delayOn) {
                if(correctPath()){
                    ++score;
                    //record correct
                    if(trial <= 5) {
                        trialSuccess[trial - 1] = 1;
                    }
                } else {
                    if(trial <= 5) {
                        //record incorrect
                        trialSuccess[trial - 1] = 0;
                    }
                }
                if(trial == 5) {
                    postScore();
                    game.setScreen(new EndScreen(game, score, trial));
                }
                ++trial;
                generateTrial();
            }
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
        if(!disableTouchDown) {
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    if (grid[i][j].contains(screenX, screenHeight - screenY) && nonSetRect(i, j)) {
                        selected[i][j] = !selected[i][j];
                    }
                }
            }

            if(back.contains(screenX, screenHeight - screenY)){
                onBack = true;
            }

            if(end.contains(screenX, screenHeight - screenY)){
                onEnd = true;
            }

            if(btnSubmit.contains(screenX, screenHeight - screenY)){
                onSubmit = true;
            }

            prevX = screenX;
            prevY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        if(!disableTouchDown) {
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    if (grid[i][j].contains(x, screenHeight - y) && nonSetRect(i, j)) {
                        if(! grid[i][j].contains(prevX, screenHeight - prevY)){
                            selected[i][j] = !selected[i][j];
                        }
                    }
                }
            }
            prevX = x;
            prevY = y;
        }
        return false;
    }

    private void selectState(boolean blockSelected){
        if(!blockSelected){
            renderer.setColor(GameFourConstants.TITLE_COLOR);
        } else {
            renderer.setColor(GameFourConstants.EASY_COLOR);
        }
    }


    private void generateTrial() {
        onEnd = false;
        onBack = false;
        onSubmit = false;
        elapsed = 0;
        delayed = 0;
        prevX = 0;
        prevY = 0;
        startRow = startCol = endRow = endCol = 0;
        disableTouchDown = true;
        delayOn = false;
        timerStart = true;
        selected = new boolean[blocksHorizontal][blocksVertical];
        toRemember = new boolean[blocksHorizontal][blocksVertical];

        //sets the start and end tiles
        //TODO: make the start/end blocks flexible if necessary
        /*int positionStart = (int)(Math.random()*blocksHorizontal*blocksVertical);
        while(toRemember[positionStart/blocksVertical%blocksHorizontal][positionStart%blocksVertical]){
            positionStart = (int)(Math.random()*blocksHorizontal*blocksVertical);
        }
        startRow = positionStart/blocksVertical%blocksHorizontal;
        startCol = positionStart%blocksVertical;*/

        startCol = startRow = 0;
        endRow = blocksHorizontal - 1;
        endCol = blocksVertical - 1;

        int curBlocks = 0;
        while(curBlocks < roadBlockNum){
            int position = (int)(Math.random()*blocksHorizontal*blocksVertical);
            if(!toRemember[position/blocksVertical%blocksHorizontal][position%blocksVertical] &&
                    nonSetRect(position/blocksVertical%blocksHorizontal, position%blocksVertical) &&
                    !isCorner(position/blocksVertical%blocksHorizontal, position%blocksVertical)){
                ++curBlocks;
                toRemember[position/blocksVertical%blocksHorizontal][position%blocksVertical] = true;
            }
        }

    }

    private boolean isCorner(int row, int col){
        if(row == blocksHorizontal && col == blocksVertical - 1){
            return toRemember[blocksHorizontal - 1][blocksVertical];
        }
        if(row == blocksHorizontal - 1 && col == blocksVertical ){
            return toRemember[blocksHorizontal][blocksVertical - 1];
        }

        if(row == 0 && col == 1){
            return toRemember[1][0];
        }

        if(row == 1 && col == 0){
            return toRemember[0][1];
        }
        return false;
    }

    private boolean correctPath(){
        boolean hp = hasPath();

        if(!hp){
            return false;
        } else {
            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    if (selected[i][j]) {
                        if (toRemember[i][j]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    public boolean hasPath(){
        boolean[][] temp = new boolean[blocksHorizontal][blocksVertical];
        temp[0][0] = true;

        for (int i = 0; i < blocksHorizontal; ++i) {
            for (int j = 0; j < blocksVertical; ++j) {
                if(i == 0){
                    if(j == 0){
                        if ((//temp[i + 1][j + 1] ||
                                temp[i][j + 1] ||
                                temp[i + 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }
                    } else if(j > 0 && j < blocksHorizontal - 1){
                        if ((//temp[i + 1][j - 1] ||
                                //temp[i + 1][j + 1] ||
                                temp[i][j + 1] ||
                                temp[i][j - 1] ||
                                temp[i + 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }
                    } else {
                        if((//selected[i + 1][j - 1] ||
                                selected[i][j - 1] ||
                                selected[i + 1][j]) && selected[i][j]){
                            temp[i][j] = true;
                        }
                    }
                }else if(i == blocksHorizontal - 1){
                    if(j == 0){
                        if ((//temp[i - 1][j + 1] ||
                                temp[i][j + 1] ||
                                temp[i - 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }

                    } else if(j > 0 && j < blocksHorizontal - 1){
                        if((//temp[i-1][j-1] ||
                                //temp[i-1][j+1] ||
                                temp[i][j+1] ||
                                temp[i][j-1] ||
                                temp[i-1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }

                    } else {
                        if ((//temp[i - 1][j - 1] ||
                                temp[i][j - 1] ||
                                temp[i - 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }
                    }
                } else {
                    if(j == 0){
                        if ((//temp[i - 1][j + 1] ||
                                //temp[i + 1][j + 1] ||
                                temp[i - 1][j] ||
                                temp[i][j + 1] ||
                                temp[i + 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }

                    } else if(j > 0 && j < blocksHorizontal - 1){
                        if ((//temp[i - 1][j - 1] ||
                                //temp[i - 1][j + 1] ||
                                //temp[i + 1][j - 1] ||
                                //temp[i + 1][j + 1] ||
                                temp[i][j + 1] ||
                                temp[i][j - 1] ||
                                temp[i + 1][j] ||
                                temp[i - 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }

                    } else {
                        if ((//temp[i - 1][j - 1] ||
                                temp[i][j - 1] ||
                                temp[i - 1][j]) && selected[i][j]) {
                            temp[i][j] = true;
                        }
                    }
                }
                System.out.println(Arrays.deepToString(temp) );
            }
        }

        return temp[endRow][endCol];
    }

    //Posts score and stats to MySQL database
    private void postScore() {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_spacialgame_score.php");

        //set parameters
        Map<String, String> json = new HashMap<String, String>();
        json.put("username", game.getUsername());
        json.put("time", game.getDateTime());
        json.put("location", game.getLocation());
        if (game.getLucid()) {
            json.put("menu", "Lucid");
        } else if (game.getPatient()) {
            json.put("menu", "Patient");
        } else if (game.getCare()) {
            json.put("menu", "CareGiver");
        }
        if (gameMode == 2) {
            json.put("difficulty", "Hard");
        } else if(gameMode == 1) {
            json.put("difficulty", "Medium");
        } else{
            json.put("difficulty", "Easy");
        }
        json.put("score", String.valueOf(score));
        for (int i = 0; i < trial; i++) {
            String trialNum = "trial" + (i + 1);
            json.put(trialNum, String.valueOf(trialSuccess[i]));
            json.put(trialNum + "time", String.valueOf(trialTime[i]));
        }

        httpPost.setContent(HttpParametersUtils.convertHttpParameters(json));

        //Send JSON and Look for response
        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String status = httpResponse.getResultAsString().trim();
                HashMap<String, String> map = new Gson().fromJson(status, new TypeToken<HashMap<String, String>>() {
                }.getType());
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



    private boolean nonSetRect(int rowNum, int colNum){
        return (rowNum != startRow || colNum != startCol) && (rowNum != endRow || colNum != endCol);
    }

    //Posts score and stats to MySQL database
    /*private void postScore(){
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_nametofacegame_score.php");

        //set parameters
        Map<String, String> json = new HashMap<String, String>();
        json.put("username", game.getUsername());
        json.put("time", game.getDateTime());
        json.put("location", game.getLocation());
        if(game.getLucid()){
            json.put("menu", "Lucid");
        } else if (game.getPatient()) {
            json.put("menu", "Patient");
        } else if (game.getCare()) {
            json.put("menu", "CareGiver");
        }
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
    }*/
}
