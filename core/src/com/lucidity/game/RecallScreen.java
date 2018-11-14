package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
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

import org.w3c.dom.css.Rect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
    ArrayList<String> locations;

    Rectangle end, back;
    Rectangle[] answers;
    String[] choices;
    int numOfAnswer = 3;
    boolean[] onSelectAns;
    boolean onEnd, onBack, hasAdded, isCorrect = false;
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
        locations = game.getLivedlocations();

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
                        font.setColor(BlockGameConstants.CORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, ObjectGameConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight* 3 / 4);

                        final GlyphLayout promptLayout = new GlyphLayout(font, FacialGameConstants.CORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight* 3 / 4 + 1.5f * reactionLayout.height);
                        isCorrect = true;


                    } else {
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

            font.draw(batch, FacialGameConstants.SCORE_LABEL + Integer.toString(score),
                    FacialGameConstants.SCORE_CENTER, screenHeight - FacialGameConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, FacialGameConstants.SCORE_LABEL);

            font.draw(batch, FacialGameConstants.TRIAL_LABEL + Integer.toString(trial),
                    FacialGameConstants.SCORE_CENTER,
                    screenHeight - FacialGameConstants.SCORE_CENTER - layout_scores.height * 1.5f);

            batch.end();

            if(elapsed - delayed >= 1f && delayOn) {
                if(isCorrect){
                    if(!hasAdded){
                        score++;
                        if(trial <= 5) {
                            trialSuccess[trial - 1] = 1;
                        }
                        hasAdded = true;
                    } else {
                        if(trial <= 5) {
                            trialSuccess[trial - 1] = 0;
                        }
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
        isCorrect = false;
        hasAdded = false;
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
        } else if(gameMode.equals("location")){
            int correctPos = (int )(Math.random() * locations.size());
            correct = locations.get(correctPos);
            choices[(int)(Math.random() * numOfAnswer)] = correct;
            selected.add(correct);

            for(int i = 0; i < numOfAnswer; i++){
                if(choices[i] == null){
                    String filler = RecallGameConstants.RANDOM_LOCATION_NAMES[(int )(Math.random() * RecallGameConstants.RANDOM_LOCATION_NAMES.length)];
                    while(selected.contains(filler)){
                        filler = RecallGameConstants.RANDOM_LOCATION_NAMES[(int )(Math.random() * RecallGameConstants.RANDOM_LOCATION_NAMES.length)];
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

    //Posts score and stats to MySQL database
    private void postScore() {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_recallgame_score.php");

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
        json.put("mode", gameMode);
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
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    //success
                    String status = httpResponse.getResultAsString().trim();
                    HashMap<String, String> map = new Gson().fromJson(status, new TypeToken<HashMap<String, String>>() {
                    }.getType());
                    System.out.println(map);

                    game.scorePoster.postOnline(game.getUsername());

                } else {
                    // save scores locally
                    game.scorePoster.postScoreRe(game.getUsername(), game.getDateTime(), game.getLocation(),
                            menu, gameMode, score, trialSuccess, trialTime);
                }
            }

            public void failed(Throwable t) {
                String status = "failed";
                // save scores locally
                game.scorePoster.postScoreRe(game.getUsername(), game.getDateTime(), game.getLocation(),
                        menu, gameMode, score, trialSuccess, trialTime);
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
