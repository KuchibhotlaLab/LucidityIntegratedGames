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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.w3c.dom.css.Rect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lixiaoyan on 10/8/18.
 */

//TODO: populate this class
public class RecallScreen extends InputAdapter implements Screen {

    private RecallGame game;

    private ExtendViewport viewport;
    private ScreenViewport hudViewport;

    private int screenWidth, screenHeight;
    private int score, trial;
    private int maxTrial = 5;

    private ShapeRenderer renderer;

    String username;
    ArrayList<String> imgNames;
    ArrayList<ArrayList<String>> imgTags;
    ArrayList<String> imgGenders;

    Rectangle end, back;
    Rectangle[] answers;
    String[] choices;
    int numOfAnswer = 3;
    boolean[] onSelectAns;
    boolean onEnd, onBack = false;
    String correct;
    String correctPrompt;
    String gameMode;


    Sprite display;
    SpriteBatch batch;
    BitmapFont font;

    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    float elapsed = 0;
    //cheap fix
    boolean delayOn= false;
    float delayed = -10000;

    private boolean disableTouchDown=true;

    public RecallScreen(RecallGame game, String mode) {
        this.game = game;
        this.gameMode = mode;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        int width = screenWidth * 2/ 3;
        int height = screenHeight / 12;
        int offset = screenHeight/ 24;
        answers = new Rectangle[numOfAnswer];
        for(int i = 0; i < answers.length; i++){
            Rectangle answer = new Rectangle();

            answer.width = width;
            answer.height = height;
            answer.x = screenWidth / 6;
            answer.y = i * answer.height + screenHeight / 10 + offset * i;
            answers[i] = answer;
        }

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;


        imgNames = game.getPicturenames();
        imgTags = game.getPicturetags();
        imgGenders = game.getPicturegenders();
        username = game.getUsername();

        timerStart = true;
        trialTime = new double[maxTrial];
        trialSuccess = new int[maxTrial];
        trial = 1;


        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        generateTrial();
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(RecallGameConstants.WORLD_SIZE, RecallGameConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        viewport.apply(true);
        Gdx.gl.glClearColor(RecallGameConstants.BACKGROUND_COLOR.r, RecallGameConstants.BACKGROUND_COLOR.g, RecallGameConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;

        if(elapsed < 2) {
            batch.begin();
            font.getData().setScale(RecallGameConstants.INSTRUCTION_SIZE);
            font.setColor(RecallGameConstants.TITLE_COLOR);

            if (gameMode.equals("relation")) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, RecallGameConstants.PROMPT_RELATION);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight / 2);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, RecallGameConstants.PROMPT_ONE + gameMode);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight / 2 + 1.5f * promptLayout_two.height);

            } else if (gameMode.equals("location")) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, RecallGameConstants.PROMPT_LOCATION);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight / 2);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, RecallGameConstants.PROMPT_ONE + gameMode);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight / 2 + 1.5f * promptLayout_two.height);

            }

            batch.end();

        } else {
            //TODO: finish implementing game
            //Start timer
            if (timerStart) {
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }

            batch.begin();
            font.getData().setScale(RecallGameConstants.INSTRUCTION_SIZE);
            font.setColor(RecallGameConstants.TITLE_COLOR);
            String prompt = "";

            if(gameMode.equals("relation")){
                prompt  = "Who" +  RecallGameConstants.PROMPT_TWO + correctPrompt + "?";
            } else if (gameMode.equals("location")){
                prompt  = "Where" +  RecallGameConstants.PROMPT_TWO + "?";
            }

            final GlyphLayout promptLayout_next = new GlyphLayout(font, prompt);
            font.draw(batch, promptLayout_next, (screenWidth - promptLayout_next.width) / 2,
                    screenHeight * 7 / 8);
            batch.end();


            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for(int i = 0; i < answers.length; i++){
                if(!onSelectAns[i]){
                    renderer.setColor(FacialGameConstants.W2F_COLOR);
                } else {
                    disableTouchDown = true;
                    renderer.end();
                    batch.begin();

                    if(!delayOn){
                        delayOn = true;
                        delayed = elapsed;

                        //record reaction time here
                        if(trial <= 5) {
                            trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                        }
                    }

                    if(choices[i].equals(correct)){
                        ++score;

                        //record correct
                        if(trial <= maxTrial) {
                            trialSuccess[trial - 1] = 1;
                        }
                        font.setColor(BlockGameConstants.CORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, ObjectGameConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight* 3 / 4);

                        final GlyphLayout promptLayout = new GlyphLayout(font, FacialGameConstants.CORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight* 3 / 4 + 1.5f * reactionLayout.height);
                    } else {
                        if(trial <= maxTrial) {
                            //record incorrect
                            trialSuccess[trial - 1] = 0;
                        }
                        font.setColor(BlockGameConstants.INCORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, ObjectGameConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight* 3 / 4);

                        final GlyphLayout promptLayout = new GlyphLayout(font, FacialGameConstants.INCORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight * 3 / 4 + 1.5f * reactionLayout.height);
                    }
                    batch.end();
                    renderer.begin(ShapeRenderer.ShapeType.Filled);
                    renderer.setColor(FacialGameConstants.CHOICE_COLOR);
                }
                renderer.rect(answers[i].x, answers[i].y, answers[i].getWidth(), answers[i].getHeight());
            }

            if(!onEnd){
                renderer.setColor(FacialGameConstants.W2F_COLOR);
            } else {
                renderer.setColor(FacialGameConstants.CHOICE_COLOR);
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
                renderer.setColor(FacialGameConstants.W2F_COLOR);
            } else {
                renderer.setColor(FacialGameConstants.CHOICE_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {game.setScreen(new ModeScreen(game));
                                   }
                               },
                        1);
            }
            renderer.rect(back.x, back.y, back.width, back.height);

            renderer.end();

            renderer.begin(ShapeRenderer.ShapeType.Line);

            renderer.setColor(FacialGameConstants.OUTLINE_COLOR);
            for(int i = 0; i < answers.length; i++){
                renderer.rect(answers[i].x, answers[i].y, answers[i].getWidth(), answers[i].getHeight());
            }
            renderer.rect(end.x, end.y, end.width, end.height);
            renderer.rect(back.x, back.y, back.width, back.height);

            renderer.end();

            batch.begin();
            font.setColor(RecallGameConstants.TITLE_COLOR);
            for(int i  = 0; i < numOfAnswer; i++){
                final GlyphLayout answerLayout = new GlyphLayout(font, choices[i]);
                font.draw(batch, choices[i], answers[i].x + answers[i].width/2, answers[i].y + answers[i].height/2 + answerLayout.height/3, 0, Align.center, false);
            }

            //TODO: change back and text scale
            font.getData().setScale(FacialGameConstants.ANSWER_SCALE);
            font.draw(batch, FacialGameConstants.BACK_TEXT,
                    (int) (back.x + 0.2 * back.getWidth()),
                    (int) (back.y + 0.6 * back.getHeight()));

            //prints text on end button
            font.draw(batch, FacialGameConstants.END_TEXT,
                    (int) (end.x + 0.25 * end.getWidth()),
                    (int) (end.y + 0.6 * end.getHeight()));
            batch.end();

            if(elapsed - delayed >= 1f && delayOn) {
                if(trial == 5) {
                    //postScore();
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

    private void generateTrial(){
        elapsed = 0;
        delayed = -10000;
        delayOn = false;
        correct = null;
        correctPrompt = null;

        onSelectAns = new boolean[numOfAnswer];
        choices = new String[numOfAnswer];
        onEnd = false;
        onBack = false;
        timerStart = true;
        populateChoices();
    }

    private void populateChoices(){
        HashSet<String> selected = new HashSet<String>();
        if(gameMode.equals("relation")){
            int correctPos = (int )(Math.random() * imgTags.size());
            correct = imgTags.get(correctPos).get(1);
            correctPrompt = imgTags.get(correctPos).get(0);
            choices[(int)(Math.random() * numOfAnswer)] = correct;
            selected.add(correct);

            for(int i = 0; i < numOfAnswer; i++){
                if(choices[i] == null){
                    String filler = imgTags.get((int )(Math.random() * imgTags.size())).get(1);
                    while(selected.contains(filler)){
                        filler = imgTags.get((int )(Math.random() * imgTags.size())).get(1);
                    }
                    choices[i] = filler;
                    selected.add(filler);
                }
            }
        }

    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!disableTouchDown) {
            for(int i = 0; i < answers.length; i++){
                if(answers[i].contains(screenX, screenHeight - screenY)){
                    boolean prev = onSelectAns[i];
                    onSelectAns = new boolean[numOfAnswer];
                    onSelectAns[i] = !prev;
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
}
