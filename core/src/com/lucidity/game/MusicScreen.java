package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiaoyan on 11/16/18.
 */

public class MusicScreen extends InputAdapter implements Screen {

    private MusicGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    private int screenWidth, screenHeight;
    private int score;
    private int trial = 1;
    private int maxTrial = 5;

    private ShapeRenderer renderer;

    Sprite display;
    SpriteBatch batch;
    BitmapFont font;

    Texture playBtn, stopBtn, homeBut, returnBut, musicBtn;
    Rectangle end, back, play, answerOne, answerTwo;
    boolean onEnd, onBack, onplay, onSelectOne, onSelectTwo;
    boolean answerIsSelected, isCorrect;


    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    float elapsed = 0;
    boolean delayOn= false;
    float delayed = -10000;

    private boolean disableTouchDown=false;
    String username;
    Music music;
    ArrayList<Music> validSongs;

    private String name, author, answer, attrOne, attrTwo;
    private int questionNumber = 0;
    private String promptOne, promptTwo;


    public MusicScreen(MusicGame game) {
        this.game = game;
        username = game.getUsername();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);

        playBtn = new Texture(Gdx.files.internal("data/playBtn.png"));
        stopBtn = new Texture(Gdx.files.internal("data/stopBtn.png"));
        homeBut = new Texture(Gdx.files.internal("data/homeBtn.png"));
        returnBut = new Texture(Gdx.files.internal("data/returnBtn.png"));

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = end.height;//screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;

        play = new Rectangle();
        play.width = play.height = end.height;
        play.x = screenWidth / 4;
        //play.y = screenHeight * 2 / 3;
        play.y = screenHeight / 2;

        answerOne = new Rectangle();
        answerTwo = new Rectangle();
        answerOne.height = answerTwo.height = screenHeight / 12;
        answerOne.width = answerTwo.width = screenWidth / 2;
        answerOne.x = answerTwo.x = screenWidth / 4;
        answerOne.y = screenHeight / 10;
        answerTwo.y = answerOne.y + answerOne.height;

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];

        validSongs = new ArrayList<Music>();

        String locRoot = "data/user/0/com.lucidity.game/app_audioDir/" + username;
        File folder = new File(locRoot);
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles != null){
            for(File file : listOfFiles){
                if(file.isFile()){
                    if("mp3".equals(file.getName().substring(file.getName().length()-3, file.getName().length()))){
                        validSongs.add(Gdx.audio.newMusic(Gdx.files.absolute(locRoot+file.getName())));
                    }
                }
            }
        }

        generateTrial();
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(FacialGameConstants.WORLD_SIZE, FacialGameConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        viewport.apply(true);
        Gdx.gl.glClearColor(MusicGameConstants.BACKGROUND_COLOR.r, MusicGameConstants.BACKGROUND_COLOR.g, MusicGameConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBackground();
        renderer.end();
        elapsed += delta;
        if(elapsed < 2) {
            batch.begin();
            font.getData().setScale(FacialGameConstants.PROMPT_SCALE);

            if (trial == 1) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, FacialGameConstants.PROMPT_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight / 2);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, FacialGameConstants.PROMPT_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight / 2 + 1.5f * promptLayout_two.height);
            } else {
                final GlyphLayout promptLayout_next = new GlyphLayout(font, BlockGameConstants.PROMPT_NEXT);
                font.draw(batch, promptLayout_next, (screenWidth - promptLayout_next.width) / 2,
                        screenHeight / 2);
            }

            batch.end();

        } else {
            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(FacialGameConstants.PROMPT_SCALE);

            final GlyphLayout promptLayout_two = new GlyphLayout(font, promptTwo);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight * 3/ 4);

            final GlyphLayout promptLayout_one = new GlyphLayout(font, promptOne);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                    screenHeight * 3/ 4 + 1.5f * promptLayout_two.height);

            triggerEnd();
            triggerBack();
            batch.draw(musicBtn, play.x, play.y, play.width, play.height);
            //playButton.draw(batch, 1);
            batch.end();

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            drawAnswerButton(onSelectOne, answerOne);
            drawAnswerButton(onSelectTwo, answerTwo);
            renderer.end();

            if(!delayOn && answerIsSelected){
                delayOn = true;
                delayed = elapsed;

                //record reaction time here
                if(trial <= 5) {
                    trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                }
            }


            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(FacialGameConstants.ANSWER_SCALE);
            final GlyphLayout layout_two = new GlyphLayout(font, attrTwo);
            final float fontX_two = (screenWidth - layout_two.width) / 2;
            final float fontY_two = (answerTwo.height * 0.6f + answerTwo.y);
            font.draw(batch, layout_two, fontX_two, fontY_two);

            final GlyphLayout layout_one = new GlyphLayout(font, attrOne);
            final float fontX_one = (screenWidth - layout_one.width) / 2;
            final float fontY_one = (answerOne.height * 0.6f + answerOne.y);
            font.draw(batch, layout_one, fontX_one, fontY_one);

            font.draw(batch, FacialGameConstants.SCORE_LABEL + Integer.toString(score),
                    FacialGameConstants.SCORE_CENTER, screenHeight - FacialGameConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, FacialGameConstants.SCORE_LABEL);
            font.draw(batch, FacialGameConstants.TRIAL_LABEL + Integer.toString(trial),
                    FacialGameConstants.SCORE_CENTER,
                    screenHeight - FacialGameConstants.SCORE_CENTER - layout_scores.height * 1.5f);

            final GlyphLayout layout_trials = new GlyphLayout(font, FacialGameConstants.TRIAL_LABEL);
            font.draw(batch, "Question Number:"+ Integer.toString(questionNumber),
                    FacialGameConstants.SCORE_CENTER,
                    screenHeight - FacialGameConstants.SCORE_CENTER - layout_scores.height * 1.5f - layout_trials.height * 1.5f);

            batch.end();
            if(elapsed - delayed >= 1f && delayOn) {
                computeScore();
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
        music.dispose();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(play.contains(screenX, screenHeight - screenY)){
            onplay = !onplay;
            if(onplay){
                music.stop();
                musicBtn = playBtn;
            } else {
                music.play();
                musicBtn = stopBtn;
            }
        }
        if(!disableTouchDown) {
            if(back.contains(screenX, screenHeight - screenY)){
                onBack = true;
            }

            if(end.contains(screenX, screenHeight - screenY)){
                onEnd = true;
            }

            if (answerOne.contains(screenX, screenHeight - screenY)) {
                onSelectOne = !onSelectOne;
                onSelectTwo = false;
                disableTouchDown = true;
                answerIsSelected = true;
                batch.begin();
                triggerNextRound(attrOne);
                batch.end();
            }
            if (answerTwo.contains(screenX, screenHeight - screenY)) {
                onSelectTwo = !onSelectTwo;
                onSelectOne = false;
                disableTouchDown = true;
                answerIsSelected = true;
                batch.begin();
                triggerNextRound(attrTwo);
                batch.end();
            }
        }
        return true;
    }

    private void computeScore(){
        if(isCorrect && answerIsSelected) {
            ++score;
            isCorrect = false;

            //record correct
            if(trial <= 5) {
                trialSuccess[trial - 1] +=1;
            }
        }
        if(trial == 5) {
            triggerEnd();
            //postScore();
            //game.setScreen(new EndScreen(game, score, trial));
        }
        if(questionNumber >= 3){
            ++trial;
            questionNumber = 0;
        }
        generateTrial();
    }

    private void generateTrial(){
        music = Gdx.audio.newMusic(Gdx.files.absolute("data/user/0/com.lucidity.game/app_audioDir/Observent.mp3"));
        name = "Observent";
        author = "Florent Mothe";
        musicBtn = playBtn;
        questionNumber++;

        elapsed = 0;
        delayed = -10000;
        delayOn = false;

        onSelectOne = false;
        onSelectTwo = false;
        onEnd = false;
        onBack = false;
        timerStart = true;
        isCorrect = false;
        answerIsSelected = false;
        disableTouchDown = false;


        setQuestion();
        setAnswer();
    }

    private void drawAnswerButton(boolean selected, Rectangle answer){
        if(!selected){
            renderer.setColor(FacialGameConstants.W2F_COLOR);
        } else {
            renderer.setColor(FacialGameConstants.CHOICE_COLOR);
        }
        renderer.rect(answer.x, answer.y, answer.getWidth(), answer.getHeight());
    }

    private void setQuestion(){
        switch (questionNumber) {
            case 1:
                promptOne = "Have your heard";
                promptTwo = "of this song before?";
                break;
            case 2:
                promptOne = "What is the name";
                promptTwo = "of this song?";
                break;
            case 3:
                promptOne = "Who is the author";
                promptTwo = "of this song?";
                break;
        }
    }

    private void setAnswer(){
        switch (questionNumber) {
            case 1:
                answer = "True";
                break;
            case 2:
                answer = name;
                break;
            case 3:
                answer = author;
                break;
        }
        int position = (int) (Math.random() * 2);
        if(position == 0) {
            attrOne = answer;
            attrTwo = "False";
        } else {
            attrOne = "False";
            attrTwo = answer;
        }
    }

    private void triggerBack(){
        if(onBack){
            disableTouchDown = true;
            homeBut = new Texture(Gdx.files.internal("data/homeBtnPressed.png"));
            /*Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   game.setScreen(new EndScreen(game, score, trial));
                               }
                           },
                    1);*/
        }
        batch.draw(homeBut, back.x, back.y, back.width, back.height);

    }

    private void triggerEnd(){
        if(onEnd){
            disableTouchDown = true;
            returnBut = new Texture(Gdx.files.internal("data/returnBtnPressed.png"));
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {Gdx.app.exit();//game.setScreen(new DifficultyScreen(game));
                               }
                           },
                    1);
        }
        batch.draw(returnBut, end.x, end.y, end.width, end.height);

    }

    private void triggerNextRound(String attr){
        if(answer.equals(attr)){
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
    }

    private void drawBackground(){
        renderer.setColor(MusicGameConstants.DARK_BLUE);
        renderer.circle(screenWidth * 4/ 5, screenHeight/4, screenHeight/4);
        renderer.setColor(MusicGameConstants.LIGHT_GRAY);
        renderer.circle(screenWidth* 4/ 5, screenHeight/4, screenHeight/6);


        renderer.setColor(MusicGameConstants.DARK_BLUE);
        renderer.circle(-screenWidth/7, screenHeight/4, screenHeight/6);
        renderer.setColor(MusicGameConstants.LIGHT_GRAY);
        renderer.circle(-screenWidth/7, screenHeight/4, screenHeight /9);

        renderer.setColor(MusicGameConstants.DARK_BLUE);
        renderer.circle(screenWidth/5, screenHeight, screenHeight/8);
        renderer.setColor(MusicGameConstants.LIGHT_GRAY);
        renderer.circle(screenWidth/5, screenHeight, screenHeight /12);

    }

    /*private void postScore() {
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
        //json.put("mode", gameMode);
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
                    //game.scorePoster.postScoreRe(game.getUsername(), game.getDateTime(), game.getLocation(),
                    //        menu, gameMode, score, trialSuccess, trialTime);
                }
            }

            public void failed(Throwable t) {
                String status = "failed";
                // save scores locally
                //game.scorePoster.postScoreRe(game.getUsername(), game.getDateTime(), game.getLocation(),
                //        menu, gameMode, score, trialSuccess, trialTime);
            }

            @Override
            public void cancelled() {

            }
        });

        if(game.getCare() || game.getLucid()) {
            game.scorePoster.updateTestRun(game.getUsername(), game.actionResolver.getCounter(), game.getDateTime());
        }
    }*/

}
