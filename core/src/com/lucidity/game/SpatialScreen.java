package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.sqrt;

/**
 * Created by lixiaoyan on 7/20/18.
 */

public class SpatialScreen extends InputAdapter implements Screen {
    private SpatialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth, screenHeight;
    int score, trial;

    ShapeRenderer renderer;

    SpriteBatch batch;
    Texture background, homeBut, returnBut;
    TextureRegion textureRegion;
    Sprite resizedBg;
    BitmapFont font;

    Pixmap pm;
    Texture tx;

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
    boolean[][] selected, toRemember;
    int blocksHorizontal, blocksVertical;

    int roadBlockNum;
    int prevX, prevY;

    Rectangle btnSubmit;
    boolean onSubmit = false;

    int startRow, startCol, endRow, endCol;

    private boolean disableTouchDown = true;
    private boolean delayOn = false;

    public SpatialScreen(SpatialMemoryGame game, int mode) {
        this.game = game;
        score = 0;
        trial = 1;

        gameMode = mode;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        textureRegion= new TextureRegion(background, 0, 0, background.getWidth(), background.getHeight());
        resizedBg = new Sprite(textureRegion);
        resizedBg.setSize(1f,  resizedBg.getHeight() / resizedBg.getWidth());

        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        homeBut = new Texture(Gdx.files.internal("data/homeBtn.png"));
        returnBut = new Texture(Gdx.files.internal("data/returnBtn.png"));

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = end.height;
        end.y = back.y = screenHeight - end.height - 30;
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
        btnSubmit.y = screenHeight / 12;

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
                //block.x = i * block.width + screenWidth / 12 + offset * i;
                //block.y = j * block.height + screenHeight  / 4 + offset * j;
                block.x = i * block.width + startX + offset * i;
                block.y = j * block.height + screenHeight  / 4 + offset * j;
                grid[i][j] = block;
                selected[i][j] = false;
            }
        }

        pm = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
        pm.drawPixel(0, 0, Color.rgba8888(0.12f, 0.19f, 0.29f, 1));
        pm.drawPixel(0, 1, Color.rgba8888(0.12f, 0.19f, 0.29f, 1));
        pm.drawPixel(1, 0, Color.rgba8888(0.22f, 0.44f, 0.52f, 1));
        pm.drawPixel(1, 1, Color.rgba8888(0.22f, 0.44f, 0.52f, 1));
        tx = new Texture(pm);
        tx.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);



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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(resizedBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float width = (float)(sqrt(2) * (grid[blocksVertical-1][blocksHorizontal-1].x + grid[blocksVertical-1][blocksHorizontal-1].width - grid[0][0].x));
        batch.draw(new TextureRegion(tx), grid[blocksHorizontal-1][0].x + grid[blocksHorizontal-1][0].width, grid[0][blocksVertical-1].y+grid[0][blocksVertical-1].height, 0, 0,  width, screenHeight, 1f, 1f, 225, false);

        batch.end();
        elapsed += delta;

        if(elapsed < 2f) {
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
                    drawRoadBlock(toRemember[i][j]);
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


        } else if(elapsed == 2f) {
            selected = new boolean[blocksHorizontal][blocksVertical];
        } else if (elapsed < 3.5f) {

            batch.begin();

            final GlyphLayout layout_two = new GlyphLayout(font, GameFourConstants.INSTRUCTIONS_TWO);
            font.draw(batch, layout_two, (screenWidth - layout_two.width) / 2, screenHeight  / 8);

            batch.end();

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            selected[startRow][startCol] = true;
            selected[endRow][endCol] = true;

            for (int i = 0; i < blocksHorizontal; i++) {
                for (int j = 0; j < blocksVertical; j++) {
                    if(!selected[i][j]){
                        renderer.setColor(GameFourConstants.SQUARE_COLOR);
                    } else {
                        renderer.setColor(GameFourConstants.START_END_COLOR);
                    }
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
                    if (nonSetRect(i,j)) {
                        selectState(selected[i][j]);
                        renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                    } else {
                        renderer.setColor(GameFourConstants.START_END_COLOR);
                        renderer.rect(grid[i][j].x, grid[i][j].y, grid[i][j].getWidth(), grid[i][j].getHeight());
                    }
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
                renderer.setColor(GameFourConstants.SQUARE_COLOR);
            } else {
                renderer.setColor(GameFourConstants.SELECTED_COLOR);
                onSubmit = false;
                delayed = elapsed;
                delayOn = true;
                disableTouchDown = true;
                if(trial <= 5) {
                    trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                }
            }
            if(delayOn){
                renderer.setColor(GameFourConstants.SQUARE_COLOR);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            selectState(onSubmit);
            renderer.rect(btnSubmit.x, btnSubmit.y, btnSubmit.width, btnSubmit.height);
            renderer.end();

            batch.begin();
            triggerEnd();
            triggerBack();
            batch.end();

            /*renderer.begin();
            if(!onEnd){
                renderer.setColor(GameFourConstants.SQUARE_COLOR);
            } else {
                disableTouchDown = true;
                renderer.setColor(GameFourConstants.SELECTED_COLOR);
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
                renderer.setColor(GameFourConstants.SQUARE_COLOR);
            } else {
                disableTouchDown = true;
                renderer.setColor(GameFourConstants.SELECTED_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {game.setScreen(new DifficultyScreen(game));
                                   }
                               },
                        1);
            }
            renderer.rect(back.x, back.y, back.width, back.height);
            renderer.end();*/

            batch.begin();
            font.getData().setScale(GameThreeConstants.ANSWER_SCALE);
            /*font.setColor(Color.WHITE);
            //prints text on back button
            font.draw(batch, GameTwoConstants.BACK_TEXT,
                    (int) (back.x + 0.2 * back.getWidth()),
                    (int) (back.y + 0.6 * back.getHeight()));
            //prints text on end button
            font.draw(batch, GameTwoConstants.END_TEXT,
                    (int) (end.x + 0.25 * end.getWidth()),
                    (int) (end.y + 0.6 * end.getHeight()));*/

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
            batch.end();

            if (delayOn) {
                resultMessage(correctPath());
            }


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

    private void triggerEnd(){
        if(onEnd){
            disableTouchDown = true;
            homeBut = new Texture(Gdx.files.internal("data/homeBtnPressed.png"));
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   game.setScreen(new EndScreen(game, score, trial));
                               }
                           },
                    1);
        }
        batch.draw(homeBut, back.x, back.y, back.width, back.height);

    }

    private void triggerBack(){
        if(onBack){
            disableTouchDown = true;
            returnBut = new Texture(Gdx.files.internal("data/returnBtnPressed.png"));
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {game.setScreen(new DifficultyScreen(game));
                               }
                           },
                    1);
        }
        batch.draw(returnBut, end.x, end.y, end.width, end.height);

    }

    private void selectState(boolean blockSelected){
        if(!blockSelected){
            renderer.setColor(GameFourConstants.SQUARE_COLOR);
        } else {
            renderer.setColor(GameFourConstants.SELECTED_COLOR);
        }
    }

    private void drawRoadBlock(boolean blockSelected){
        if(!blockSelected){
            renderer.setColor(GameFourConstants.SQUARE_COLOR);
        } else {
            renderer.setColor(GameFourConstants.ROADBLOCK_COLOR);
        }
    }

    private void resultMessage(boolean isCorrect){
        if(isCorrect){
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(GameOneConstants.CORRECT_COLOR);
            renderer.rect(0, screenHeight / 3, screenWidth, screenHeight/4);

            renderer.setColor(Color.valueOf("d8d8d8"));
            renderer.rect(screenHeight/36, screenHeight / 3 + screenHeight/36, screenWidth - screenHeight/18, screenHeight/4 -  screenHeight/18);
            renderer.end();

            batch.begin();
            font.getData().setScale(GameFourConstants.RESULT_SIZE);
            font.setColor(GameFourConstants.ROADBLOCK_COLOR);
            final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.CORRECT_MESSAGE);
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 3  + screenHeight/6);
            final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
            font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2,  screenHeight / 3  + screenHeight/6- 1.5f * promptLayout.height);
            batch.end();
        } else {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(GameOneConstants.INCORRECT_COLOR);
            renderer.rect(0, screenHeight / 3, screenWidth, screenHeight/4);

            renderer.setColor(Color.valueOf("d8d8d8"));
            renderer.rect(screenHeight/36, screenHeight / 3 + screenHeight/36, screenWidth - screenHeight/18, screenHeight/4 -  screenHeight/18);
            renderer.end();

            batch.begin();
            font.setColor(GameFourConstants.ROADBLOCK_COLOR);
            font.getData().setScale(GameFourConstants.RESULT_SIZE);
            final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.INCORRECT_MESSAGE);
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 3  + screenHeight/6);
            final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
            font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 3  + screenHeight/6 +  - 1.5f * promptLayout.height);
            batch.end();

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
        } else if(row == blocksHorizontal - 1 && col == blocksVertical ){
            return toRemember[blocksHorizontal][blocksVertical - 1];
        } else if(row == 0 && col == 1){
            return toRemember[1][0];
        } else if(row == 1 && col == 0){
            return toRemember[0][1];
        } else {
            return false;
        }
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

                    } else if(j < blocksHorizontal - 1){
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
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_spatialgame_score.php");

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
