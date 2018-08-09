package com.lucidity.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiaoyan on 7/13/18.
 */

public class ObjectRecognitionScreen extends InputAdapter implements Screen {
    ObjectRecognitionGame game;

    ShapeRenderer renderer;
    SpriteBatch batch;
    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth;
    int screenHeight;

    BitmapFont font;
    float elapsed, delayed;
    ArrayList<ArrayList<Integer>> shapes;
    ArrayList<Color> colors;
    boolean hasColor = false;
    int difficult;
    boolean disabled = true;
    boolean delayOn = false;

    ArrayList<Integer> sCorrect, sShown;
    Color cCorrect, cShown;
    boolean sSame, cSame;

    Rectangle same, diff;
    boolean sameSelected, diffSelected = false;

    Rectangle end, back;
    boolean onEnd, onBack = false;

    int score, trial;

    ArrayList<Integer> sShowOne, sShowTwo;
    Color cShowOne, cShowTwo;
    boolean selectOne, selectTwo, selectThree, selectFour = false;
    Rectangle one, two, three, four;
    boolean cIsCorrect, sIsCorrect = false;

    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    public ObjectRecognitionScreen(ObjectRecognitionGame game, int level) {
        this.difficult = level;
        this.game = game;
        if(difficult == 1 || difficult == 2){
            hasColor = true;
        }


        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        colors = new ArrayList<Color>();
        if(hasColor){
            colors.add(Color.GREEN);
            colors.add(Color.BLUE);
            colors.add(Color.CYAN);
        }
        shapes = new ArrayList<ArrayList<Integer>>();
        shapes.add(new ArrayList<Integer>(Arrays.asList(screenWidth/6, screenHeight/3, screenWidth * 2 / 3, screenWidth * 2 / 3)));
        shapes.add(new ArrayList<Integer>(Arrays.asList(screenWidth / 2,screenHeight / 2, screenWidth/3)));
        shapes.add(new ArrayList<Integer>(Arrays.asList(screenWidth/6, screenHeight/3, screenWidth/2, screenHeight * 2 / 3, screenWidth * 5 / 6, screenHeight/3)));


        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(GameThreeConstants.MODE_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        same = new Rectangle();
        diff = new Rectangle();
        same.width = diff.width = screenWidth / 2;
        same.height = diff.height = screenHeight / 12;
        same.x = diff.x = screenWidth / 4;
        same.y = screenHeight / 8;
        diff.y = same.y + same.height;


        end = new com.badlogic.gdx.math.Rectangle();
        back = new com.badlogic.gdx.math.Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;

        trial = 1;

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];

        generateTrial();
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(GameThreeConstants.WORLD_SIZE, GameThreeConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        Gdx.gl.glClearColor(GameThreeConstants.LOADING_COLOR.r, GameThreeConstants.LOADING_COLOR.g, GameThreeConstants.LOADING_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;

        if(elapsed <= 2){

            batch.begin();
            font.getData().setScale(GameThreeConstants.PROMPT_SCALE);
            font.setColor(GameThreeConstants.TITLE_COLOR);

            final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.PROMPT_THREE);
            font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width)/2, screenHeight * 2 / 3);

            if(difficult == 1 || difficult == 2) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameThreeConstants.PROMPT_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_three.height);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.PROMPT_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);

            } else {
                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.PROMPT_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_three.height);
            }

            batch.end();

        } else if (elapsed > 2 && elapsed <= 4) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(cCorrect);
            switch (sCorrect.size()) {
                case 3:
                    renderer.circle(sCorrect.get(0), sCorrect.get(1), sCorrect.get(2));
                    break;
                case 4:
                    renderer.rect(sCorrect.get(0), sCorrect.get(1), sCorrect.get(2), sCorrect.get(3));
                    break;
                case 6:
                    renderer.triangle(sCorrect.get(0), sCorrect.get(1), sCorrect.get(2), sCorrect.get(3), sCorrect.get(4), sCorrect.get(5));
                    break;
                default:
                    break;
            }
            renderer.end();

        } else if(elapsed > 4 && elapsed <= 6){
            batch.begin();
            font.getData().setScale(GameThreeConstants.INSTRUCTION_SCALE);

            if(difficult == 2) {
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_HARD_THREE);
                font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width) / 2, screenHeight * 2 / 3);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_HARD_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_three.height);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_HARD_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_three.height + 1.5f * promptLayout_two.height);
            } else {
                final GlyphLayout promptLayout_four = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_FOUR);
                font.draw(batch, promptLayout_four, (screenWidth - promptLayout_four.width) / 2, screenHeight * 2 / 3);

                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_THREE);
                font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width) / 2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_four.height);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_three.height + 1.5f * promptLayout_four.height);

                if (difficult == 1) {
                    final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_ONE_TWO);
                    font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                            screenHeight * 2 / 3 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height + 1.5f * promptLayout_four.height);
                } else {
                    final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_ONE_ONE);
                    font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                            screenHeight * 2 / 3 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height + 1.5f * promptLayout_four.height);
                }
            }
            batch.end();

        } else {
            //Start timer
            if (timerStart){
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disabled = false;
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if(difficult == GameThreeConstants.DIFFICULTY_EASY || difficult == GameThreeConstants.DIFFICULTY_MEDIUM){
                renderer.setColor(cShown);
                switch (sShown.size()) {
                    case 3:
                        renderer.circle(sShown.get(0), sShown.get(1), sShown.get(2));
                        break;
                    case 4:
                        renderer.rect(sShown.get(0), sShown.get(1), sShown.get(2), sShown.get(3));
                        break;
                    case 6:
                        renderer.triangle(sShown.get(0), sShown.get(1), sShown.get(2), sShown.get(3), sShown.get(4), sShown.get(5));
                        break;
                    default:
                        break;
                }


                selectState(sameSelected);
                renderer.rect(same.x, same.y, same.getWidth(), same.getHeight());


                selectState(diffSelected);
                renderer.rect(diff.x, diff.y, diff.getWidth(), diff.getHeight());
                renderer.end();

                renderer.begin(ShapeRenderer.ShapeType.Line);
                renderer.setColor(GameThreeConstants.HARD_COLOR);
                renderer.rect(same.x, same.y, same.getWidth(), same.getHeight());
                renderer.rect(diff.x, diff.y, diff.getWidth(), diff.getHeight());
                renderer.end();


                if(!delayOn && (sameSelected || diffSelected)){
                    delayOn = true;
                    delayed = elapsed;

                    disabled = true;
                    //record reaction time here
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                    }
                }

                batch.begin();
                font.getData().setScale(GameThreeConstants.ANSWER_SCALE);

                if(sameSelected || diffSelected) {
                    if (isCorrect()) {
                        font.setColor(GameOneConstants.CORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight * 4 / 5);
                        final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.CORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight * 4 / 5 + 1.5f * reactionLayout.height);

                    } else {
                        font.setColor(GameOneConstants.INCORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight * 4 / 5);
                        final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight * 4 / 5  + 1.5f * reactionLayout.height);

                    }
                }

                font.setColor(Color.WHITE);
                font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                final GlyphLayout layout_two = new GlyphLayout(font, GameThreeConstants.SAME_ANSWER_TEXT);
                final float fontX_two = (screenWidth - layout_two.width) / 2;
                final float fontY_two = (same.height * 0.6f + same.y);
                font.draw(batch, layout_two, fontX_two, fontY_two);

                final GlyphLayout layout_one = new GlyphLayout(font, GameThreeConstants.DIFFERENT_ANSWER_TEXT);
                final float fontX_one = (screenWidth - layout_one.width) / 2;
                final float fontY_one = (diff.height * 0.6f + diff.y);
                font.draw(batch, layout_one, fontX_one, fontY_one);


                batch.end();


            } else {


                selectState(selectOne);
                renderer.rect(one.x, one.y, one.getWidth(), one.getHeight());

                selectState(selectTwo);
                renderer.rect(two.x, two.y, two.getWidth(), two.getHeight());

                selectState(selectThree);
                renderer.rect(three.x, three.y, three.getWidth(), three.getHeight());

                selectState(selectFour);
                renderer.rect(four.x, four.y, four.getWidth(), four.getHeight());
                renderer.end();


                renderer.begin(ShapeRenderer.ShapeType.Line);
                renderer.setColor(GameThreeConstants.HARD_COLOR);
                renderer.rect(one.x, one.y, one.getWidth(), one.getHeight());
                renderer.rect(two.x, two.y, two.getWidth(), two.getHeight());
                renderer.rect(three.x, three.y, three.getWidth(), three.getHeight());
                renderer.rect(four.x, four.y, four.getWidth(), four.getHeight());
                renderer.end();


                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(cShowOne);
                switch (sShowOne.size()) {
                    case 3:
                        renderer.circle(one.width / 2, one.width / 2 + one.y, sShowOne.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect((one.width - sShowOne.get(2) / 2) / 2, (one.height - sShowOne.get(3) / 2) / 2 + one.y, sShowOne.get(2) / 2, sShowOne.get(3) / 2);
                        break;
                    case 6:
                        int width = (sShowOne.get(4) - sShowOne.get(0)) / 2;
                        int height = (sShowOne.get(3) - sShowOne.get(1)) / 2;
                        renderer.triangle((one.width - width) / 2, one.y + (one.height - height) / 2, one.width/2, one.y + height + (one.height - height) / 2, width + (one.width - width) / 2 , one.y + (one.height - height) / 2);
                        break;
                    default:
                        break;
                }

                renderer.setColor(cShowTwo);
                switch (sShowOne.size()) {
                    case 3:
                        renderer.circle(two.width / 2, two.width / 2 + two.y, sShowOne.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect((two.width - sShowOne.get(2) / 2) / 2, (two.height - sShowOne.get(3) / 2) / 2 + two.y, sShowOne.get(2) / 2, sShowOne.get(3) / 2);
                        break;
                    case 6:
                        int width = (sShowOne.get(4) - sShowOne.get(0)) / 2;
                        int height = (sShowOne.get(3) - sShowOne.get(1)) / 2;
                        renderer.triangle((two.width - width) / 2, two.y + (two.height - height) / 2, two.width/2, two.y + height + (two.height - height) / 2, width + (two.width - width) / 2 , two.y + (two.height - height) / 2);
                        break;
                    default:
                        break;
                }

                renderer.setColor(cShowTwo);
                switch (sShowTwo.size()) {
                    case 3:
                        renderer.circle(three.width / 2 + three.x, three.width / 2 + three.y, sShowTwo.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect((three.width - sShowTwo.get(2) / 2) / 2 + three.x, (three.height - sShowTwo.get(3) / 2) / 2 + three.y, sShowTwo.get(2) / 2, sShowTwo.get(3) / 2);
                        break;
                    case 6:
                        int width = (sShowTwo.get(4) - sShowTwo.get(0)) / 2;
                        int height = (sShowTwo.get(3) - sShowTwo.get(1)) /2;
                        renderer.triangle((three.width - width) / 2 + three.x, three.y + (three.height - height) / 2, three.width/2 + three.x, three.y + height + (three.height - height) / 2, width + three.x + (three.width - width) / 2 , three.y + (three.height - height) / 2);
                        break;
                    default:
                        break;
                }

                renderer.setColor(cShowOne);
                switch (sShowTwo.size()) {
                    case 3:
                        renderer.circle(four.width / 2 + three.x, four.width / 2 + four.y, sShowTwo.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect((four.width - sShowTwo.get(2) / 2) / 2 + four.x, (four.height - sShowTwo.get(3) / 2) / 2 + four.y, sShowTwo.get(2) / 2, sShowTwo.get(3) / 2);
                        break;
                    case 6:
                        int width = (sShowTwo.get(4) - sShowTwo.get(0)) / 2;
                        int height = (sShowTwo.get(3) - sShowTwo.get(1)) /2;
                        renderer.triangle((four.width - width) / 2 + four.x, four.y + (four.height - height) / 2, four.width/2 + four.x, four.y + height + (four.height - height) / 2, width + four.x + (four.width - width) / 2 , four.y + (four.height - height) / 2);
                        break;
                    default:
                        break;
                }
                renderer.end();


                if(!delayOn && (selectOne || selectTwo || selectThree || selectFour)){
                    delayOn = true;
                    delayed = elapsed;

                    disabled = true;
                    //record reaction time here
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                    }
                }

                batch.begin();
                font.getData().setScale(GameThreeConstants.ANSWER_SCALE);

                if(selectOne || selectTwo || selectThree || selectFour) {
                    if (isCorrectHard()) {
                        font.setColor(GameOneConstants.CORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight * 4 / 5);
                        final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.CORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight * 4 / 5 + 1.5f * reactionLayout.height);

                    } else {
                        font.setColor(GameOneConstants.INCORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight * 4 / 5);
                        final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight * 4 / 5   + 1.5f * reactionLayout.height);

                    }
                }
                batch.end();

            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            if(!onEnd){
                renderer.setColor(GameThreeConstants.TITLE_COLOR);
            } else {
                disabled = true;
                renderer.setColor(GameThreeConstants.EASY_COLOR);
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
                renderer.setColor(GameThreeConstants.TITLE_COLOR);
            } else {
                disabled = true;
                renderer.setColor(GameThreeConstants.EASY_COLOR);
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

            font.setColor(GameThreeConstants.TITLE_COLOR);
            font.draw(batch, GameTwoConstants.SCORE_LABEL + Integer.toString(score),
                    GameTwoConstants.SCORE_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, GameTwoConstants.SCORE_LABEL);

            font.draw(batch, GameTwoConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameTwoConstants.SCORE_CENTER,
                    screenHeight - GameTwoConstants.SCORE_CENTER - layout_scores.height * 1.5f);
            batch.end();


            if(elapsed - delayed >= 1f && delayOn) {
                if(isCorrect() || isCorrectHard()){
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
        if(!disabled){

            if(difficult == GameThreeConstants.DIFFICULTY_EASY || difficult == GameThreeConstants.DIFFICULTY_MEDIUM) {
                if (same.contains(screenX, screenHeight - screenY)) {
                    sameSelected = !sameSelected;
                    diffSelected = false;
                }
                if (diff.contains(screenX, screenHeight - screenY)) {
                    diffSelected = !diffSelected;
                    sameSelected = false;
                }
            } else {
                if(one.contains(screenX, screenHeight - screenY)){
                    selectOne = true;
                    selectTwo = selectThree = selectFour = false;
                } else if(two.contains(screenX, screenHeight - screenY)){
                    selectTwo = true;
                    selectOne = selectThree = selectFour = false;
                } else if (three.contains(screenX, screenHeight - screenY)){
                    selectThree = true;
                    selectOne = selectTwo = selectFour = false;
                } else if(four.contains(screenX, screenHeight - screenY)){
                    selectFour = true;
                    selectOne = selectTwo = selectThree = false;
                }
            }

            if (end.contains(screenX, screenHeight - screenY)) {
                onEnd = true;
            }

            if (back.contains(screenX, screenHeight - screenY)) {
                onBack = true;
            }

        }
        return true;
    }

    private void generateTrial(){
        elapsed = 0;
        delayed = 0;
        sSame = false;
        cSame = false;
        sameSelected = false;
        diffSelected = false;
        onEnd = false;
        onBack = false;
        delayOn = false;
        timerStart = true;

        int sAnswer = (int)(Math.random() * shapes.size());
        int sShow;
        double sameChance;
        if(difficult == 0) {
            sameChance = 0.5;
        } else {
            sameChance = 0.65;
        }
        if (Math.random() < sameChance) {
            sShow = sAnswer;
        } else {
            sShow = (int) (Math.random() * shapes.size());
            while (sShow == sAnswer) {
                sShow = (int)(Math.random() * shapes.size());
            }
        }

        sSame = (sAnswer == sShow);
        sCorrect = shapes.get(sAnswer);

        if(difficult == 2) {
            while (sShow == sAnswer) {
                sShow = (int)(Math.random() * shapes.size());
            }

            initializeAnswer();
        }
        sShown = shapes.get(sShow);

        if(hasColor){
            int cAnswer = (int)(Math.random() * colors.size());
            int cShow;
            if (Math.random() < sameChance) {
                cShow = cAnswer;
            } else {
                cShow = (int) (Math.random() * colors.size());
                while (cShow == cAnswer) {
                    cShow = (int)(Math.random() * colors.size());
                }
            }

            cSame = (cAnswer == cShow);
            cCorrect = colors.get(cAnswer);

            if(difficult == 2) {
                while (cShow == cAnswer) {
                    cShow = (int)(Math.random() * shapes.size());
                }
            }
            cShown = colors.get(cShow);
        } else {
            cCorrect = GameThreeConstants.DEFAULT_COLOR;
            cShown = GameThreeConstants.DEFAULT_COLOR;
        }
        cIsCorrect = ((int)(Math.random() * 2) == 0);
        sIsCorrect = ((int)(Math.random() * 2) == 0);

        sShowOne = new ArrayList<Integer>();
        sShowTwo = new ArrayList<Integer>();

        cShowOne = new Color();
        cShowTwo = new Color();

        if(sIsCorrect){
            sShowOne = sCorrect;
            sShowTwo = sShown;
        } else {
            sShowTwo = sCorrect;
            sShowOne = sShown;
        }

        if(cIsCorrect){
            cShowOne = cCorrect;
            cShowTwo = cShown;
        } else {
            cShowTwo = cCorrect;
            cShowOne = cShown;
        }

    }

    private boolean isCorrect(){
        if(difficult == GameThreeConstants.DIFFICULTY_EASY){
            return (sameSelected && sCorrect.equals(sShown)) || (diffSelected && !sCorrect.equals(sShown));
        } else if(difficult == GameThreeConstants.DIFFICULTY_MEDIUM) {
            if(sameSelected){
                return (sCorrect.equals(sShown) && cCorrect.equals(cShown));
            } else if (diffSelected){
                return (!sCorrect.equals(sShown) || !cCorrect.equals(cShown));
            }
        }
        return false;
    }

    private boolean isCorrectHard(){
        if(selectOne){
            return (cShowOne.equals(cCorrect) && sShowOne.equals(sCorrect));
        } else if(selectTwo){
            return (cShowTwo.equals(cCorrect) && sShowOne.equals(sCorrect));
        } else if(selectThree) {
            return (cShowTwo.equals(cCorrect) && sShowTwo.equals(sCorrect));
        } else if(selectFour){
            return (cShowOne.equals(cCorrect) && sShowTwo.equals(sCorrect));
        }
        return false;
    }


    private void initializeAnswer(){
        selectOne = false;
        selectTwo = false;
        selectThree = false;
        selectFour = false;
        one = new Rectangle();
        two = new Rectangle();
        three = new Rectangle();
        four = new Rectangle();


        one.x = two.x = 0;
        three.x = four.x = screenWidth / 2;
        one.width = two.width = three.width = four.width = screenWidth / 2;
        one.height = two.height = three.height = four.height = screenWidth / 2;
        one.y = three.y = screenHeight / 10;
        two.y = four.y = three.y + three.height;


    }

    private void selectState(boolean selected){
        if(!selected){
            renderer.setColor(GameThreeConstants.TITLE_COLOR);
        } else {
            renderer.setColor(GameThreeConstants.EASY_COLOR);
        }
    }

    private boolean shapeCorrect(){
        if(selectOne || selectTwo){
            return sShowOne.equals(sCorrect);
        } else if (selectThree || selectFour){
            return sShowTwo.equals(sCorrect);
        }
        return false;
    }

    private boolean colorCorrect(){
        if(selectOne || selectFour){
            return cShowOne.equals(cCorrect);
        } else if(selectThree || selectFour){
            return cShowTwo.equals(cCorrect);
        }
        return false;
    }


    //Posts score and stats to MySQL database
    private void postScore() {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_objectrecognitiongame_score.php");

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
        if (difficult == 2) {
            json.put("difficulty", "Hard");
        } else if(difficult == 1) {
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

        /* TODO: for posting */
        boolean shapeCorrect = shapeCorrect();
        boolean colorCorrect = colorCorrect();
        /* json.put("correct shape", shapeCorrect);
        *  json.put("correct color", colorCorrect);*/

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
}