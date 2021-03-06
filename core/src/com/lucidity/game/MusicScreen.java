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
import com.badlogic.gdx.math.Circle;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiaoyan on 11/16/18.
 */

public class MusicScreen extends InputAdapter implements Screen {

    private MusicGame game;

    private ExtendViewport viewport;
    private ScreenViewport hudViewport;

    private int screenWidth, screenHeight;
    private int score;
    private int trial = 1;
    private int maxTrial = 5;

    private ShapeRenderer renderer;

    private Sprite display;
    private SpriteBatch batch;
    private BitmapFont font;

    private Texture playBtn, stopBtn, homeBut, returnBut, musicBtn, volumn;
    private Rectangle end, back, answerOne, answerTwo;
    private boolean onEnd, onBack, onplay, onSelectOne, onSelectTwo;
    private boolean answerIsSelected, isCorrect;
    private Circle play;
    private boolean played = false;


    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;
    private ArrayList<Integer> songOrder;

    private float elapsed = 0;
    private boolean delayOn= false;
    private float delayed = -10000;

    private boolean disableTouchDown=false;
    private String username;
    private Music music;
    private HashMap<String, Music> validSongs;

    private String name, author, answer, filler, attrOne, attrTwo;
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
        homeBut = new Texture(Gdx.files.internal("data/homeMusic.png"));
        returnBut = new Texture(Gdx.files.internal("data/returnMusic.png"));
        volumn = new Texture(Gdx.files.internal("data/volumn.png"));

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = end.height;//screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;

        play = new Circle();
        play.setRadius(end.height);
        play.setX(screenWidth / 4);
        play.setY(screenHeight * 7 / 12);

        int gap = screenHeight / 16;

        answerOne = new Rectangle();
        answerTwo = new Rectangle();
        answerOne.height = answerTwo.height = screenHeight / 12;
        answerOne.width = answerTwo.width = screenWidth * 2 / 3;
        answerOne.x = answerTwo.x = screenWidth / 6;
        answerOne.y = screenHeight / 10;
        answerTwo.y = answerOne.y + answerOne.height + gap;

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];
        songOrder = new ArrayList<Integer>();

        validSongs = new HashMap<String, Music>();

        String locRoot = "data/user/0/com.lucidity.game/app_audioDir/" + username + "/";
        File folder = new File(locRoot);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles != null){
            for(File file : listOfFiles){
                if(file.isFile()){
                    if("mp3".equals(file.getName().substring(file.getName().length()-3, file.getName().length()))){
                        validSongs.put(file.getName().substring(0, file.getName().length()-4), Gdx.audio.newMusic(Gdx.files.absolute(locRoot+file.getName())));
                    }
                }
            }
        }
        //Initializes ordering of five songs for trials with no repeats
        int numValidFiles = validSongs.size();
        for (int i = 0; i < 5; i++) {
            int temp = (int) (Math.random() * numValidFiles);
            if (i < numValidFiles) {
                while (songOrder.contains(temp)) {
                    temp = (int) (Math.random() * numValidFiles);
                }
            }
            songOrder.add(temp);

        }

        setSong();
        generateQuestion();
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
        elapsed += delta;
        if(elapsed < 2) {
            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(FacialGameConstants.PROMPT_SCALE);

            if (trial == 1) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, MusicGameConstants.PROMPT_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight / 2);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, MusicGameConstants.PROMPT_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight / 2 + 1.5f * promptLayout_two.height);
            } else {
                final GlyphLayout promptLayout_next = new GlyphLayout(font, BlockGameConstants.PROMPT_NEXT);
                font.draw(batch, promptLayout_next, (screenWidth - promptLayout_next.width) / 2,
                        screenHeight / 2);
            }

            batch.end();

        } else {
            if (timerStart) {
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }

            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(FacialGameConstants.PROMPT_SCALE);
            final GlyphLayout promptLayout_two = new GlyphLayout(font, promptTwo);
            final GlyphLayout promptLayout_one = new GlyphLayout(font, promptOne);
            float largerWidth = Math.max(promptLayout_two.width, promptLayout_one.width);
            batch.end();


            renderer.begin(ShapeRenderer.ShapeType.Filled);
            drawBackground();
            drawAnswerButton(onSelectOne, answerOne);
            drawAnswerButton(onSelectTwo, answerTwo);
            drawRingedCircle("button", play.x, play.y, play.radius * 3 / 2);
            renderer.setColor(MusicGameConstants.LIGHT_GRAY);
            renderer.rect((screenWidth - largerWidth) / 2 - screenWidth/20, screenHeight * 3/ 4 -  1.5f * promptLayout_two.height,
                    largerWidth + screenWidth/10, 1.75f * (promptLayout_two.height + promptLayout_one.height) );
            renderer.end();

            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(0.5f);
            if(trial == 1 && !played){
                final GlyphLayout promptLayout_once_one = new GlyphLayout(font, "Press to");
                final GlyphLayout promptLayout_once_two= new GlyphLayout(font, "play music");
                font.draw(batch, promptLayout_once_two, play.x - play.radius * 1.25f, play.y - play.radius * 2.5f);
                font.draw(batch, promptLayout_once_one, play.x - play.radius,
                        play.y - play.radius * 2.5f + 1.5f * promptLayout_two.height);
            }
            font.getData().setScale(FacialGameConstants.PROMPT_SCALE);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight * 3/ 4);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                    screenHeight * 3/ 4 + 1.5f * promptLayout_two.height);

            triggerEnd();
            triggerBack();
            batch.draw(musicBtn, play.x - play.radius/2, play.y - play.radius/2, play.radius, play.radius);
            batch.draw(volumn, play.x + play.radius * 2, play.y - play.radius * 3 / 4, screenWidth / 3, play.radius * 4 / 3);
            //playButton.draw(batch, 1);
            batch.end();

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

            if(onSelectOne){
                outputResponse(attrOne);
            }
            if(onSelectTwo){
                outputResponse(attrTwo);
            }

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
                played = true;
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
                /*batch.begin();
                triggerNextRound(attrOne);
                System.out.println("should print message for answer: " +  attrOne);
                batch.end();*/
            }
            if (answerTwo.contains(screenX, screenHeight - screenY)) {
                onSelectTwo = !onSelectTwo;
                onSelectOne = false;
                disableTouchDown = true;
                answerIsSelected = true;
                /*batch.begin();
                triggerNextRound(attrTwo);
                System.out.println("should print message for answer: " +  attrTwo);
                batch.end();*/
            }
        }
        return true;
    }

    private void computeScore(){
        music.stop();
        musicBtn = playBtn;
        if(questionNumber == 1 && !isCorrect){
            ++trial;
            setSong();
            questionNumber = 0;
            elapsed = 0;
        } else {
            if(isCorrect && answerIsSelected) {
                ++score;
                isCorrect = false;

                //record correct
                if(trial <= 5) {
                    trialSuccess[trial - 1] +=1;
                }
            }
            if(questionNumber >= 3){
                ++trial;
                setSong();
                questionNumber = 0;
                elapsed = 0;
            }
        }

        if(trial > 5) {
            Gdx.app.exit();
            //postScore();
            //game.setScreen(new EndScreen(game, score, trial));
        }
        generateQuestion();
    }

    private void adjustButtonSize(GlyphLayout text){

    }

    private void setSong(){
        music = null;
        name = "";
        author = "";
        filler = "";
        music = null;
        musicBtn = playBtn;
        boolean filled = false;
        int position = 0;
        if (trial < 6) {
            position = songOrder.get(trial - 1);
        }
        int index = -1;
        for (Map.Entry<String, Music> entry : validSongs.entrySet()) {
            index++;
            //TODO: sanitize input
            if(index == position){
                List<String> info = Arrays.asList(entry.getKey().split(" - "));
                for(String str : info){
                    System.out.println("info we should have collected: " + str);
                }
                name = info.get(0);
                author = info.get(1);
                music = entry.getValue();
                filled = true;
            } else {
                if("".equals(filler)){
                    filler = entry.getKey();
                }
                if(filled){
                    break;
                }
            }

        }


    }
    private void generateQuestion(){
        musicBtn = playBtn;
        questionNumber++;
        delayed = -10000;
        delayOn = false;
        onplay = true;

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
            renderer.setColor(MusicGameConstants.LIGHT_GRAY);
            renderer.rect(answer.x, answer.y, answer.getWidth(), answer.getHeight());

            drawCircle(MusicGameConstants.DARK_BLUE, screenWidth * 3 / 4, answer.y + answer.height / 2, answer.height / 3);
        } else {
            renderer.setColor(MusicGameConstants.TURQOIUS);
            renderer.rect(answer.x, answer.y, answer.getWidth(), answer.getHeight());
            drawCircle(MusicGameConstants.LIGHT_TURQOIUS, screenWidth * 3 / 4, answer.y + answer.height / 2, answer.height / 3);
        }
    }

    private void setQuestion(){
        switch (questionNumber) {
            case 1:
                promptOne = "Have you heard";
                promptTwo = "this song before?";
                break;
            case 2:
                promptOne = "What is the name";
                promptTwo = "of this song?";
                break;
            case 3:
                promptOne = "Who is the artist";
                promptTwo = "of this song?";
                break;
        }
    }

    private void setAnswer(){
        switch (questionNumber) {
            case 1:
                answer = "Yes";
                putAnswerInPosition(answer, "No");
                break;
            case 2:
                answer = name;
                putAnswerInPosition(answer, Arrays.asList(filler.split(" - ")).get(0));
                break;
            case 3:
                answer = author;
                putAnswerInPosition(answer, Arrays.asList(filler.split(" - ")).get(1));
                break;
        }

    }

    private void putAnswerInPosition(String ans, String fil){
        int position = (int) (Math.random() * 2);
        if(position == 0) {
            attrOne = ans;
            attrTwo = fil;
        } else {
            attrOne = fil;
            attrTwo = ans;
        }
    }

    private void triggerBack(){
        if(onBack){
            disableTouchDown = true;
            homeBut = new Texture(Gdx.files.internal("data/homeMusicPressed.png"));
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
            returnBut = new Texture(Gdx.files.internal("data/returnMusicPressed.png"));
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {Gdx.app.exit();//game.setScreen(new DifficultyScreen(game));
                               }
                           },
                    1);
        }
        batch.draw(returnBut, end.x, end.y, end.width, end.height);

    }

    private void outputResponse(String attr){
        if(answer.equals(attr)){

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(BlockGameConstants.CORRECT_COLOR);
            renderer.rect(0, screenHeight / 3, screenWidth, screenHeight/4);

            renderer.setColor(Color.valueOf("d8d8d8"));
            renderer.rect(screenHeight/36, screenHeight / 3 + screenHeight/36, screenWidth - screenHeight/18, screenHeight/4 -  screenHeight/18);
            renderer.end();

            batch.begin();
            font.getData().setScale(0.5f);
            font.setColor(MusicGameConstants.DARK_BLUE);
            final GlyphLayout promptLayout = new GlyphLayout(font, ObjectGameConstants.CORRECT_MESSAGE);
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 3  + screenHeight/6);
            final GlyphLayout reactionLayout = new GlyphLayout(font, ObjectGameConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
            font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2,  screenHeight / 3  + screenHeight/6- 1.5f * promptLayout.height);
            isCorrect = true;
            batch.end();
        } else {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(BlockGameConstants.INCORRECT_COLOR);
            renderer.rect(0, screenHeight / 3, screenWidth, screenHeight/4);

            renderer.setColor(Color.valueOf("d8d8d8"));
            renderer.rect(screenHeight/36, screenHeight / 3 + screenHeight/36, screenWidth - screenHeight/18, screenHeight/4 -  screenHeight/18);
            renderer.end();

            batch.begin();
            font.setColor(MusicGameConstants.DARK_BLUE);
            font.getData().setScale(0.5f);
            final GlyphLayout promptLayout = new GlyphLayout(font, ObjectGameConstants.INCORRECT_MESSAGE);
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 3  + screenHeight/6);
            final GlyphLayout reactionLayout = new GlyphLayout(font, ObjectGameConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
            font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 3  + screenHeight/6 +  - 1.5f * promptLayout.height);
            batch.end();
        }
    }

    private void drawBackground(){
        drawRingedCircle("background", screenWidth * 5 / 6, screenHeight/8, screenHeight/4);
        drawRingedCircle("background", -screenWidth/7, screenHeight/4, screenHeight/6);
        drawRingedCircle("background", screenWidth/5, screenHeight, screenHeight/8);
        drawRingedCircle("background", screenWidth, screenHeight * 7 / 8, screenHeight/8);
    }

    private void drawRingedCircle(String type, float x, float y, float r){
        if("background".equals(type)){
            drawCircle(MusicGameConstants.DARK_BLUE, x, y, r);
            drawCircle(MusicGameConstants.MEDIUM_GRAY, x, y, r * 2 / 3);
        } else if("button".equals(type)){
            drawCircle(MusicGameConstants.BRIGHT_ORANGE, x, y, r);
            drawCircle(MusicGameConstants.LIGHT_YELLOW, x, y, r * 2 / 3);
        }
    }

    private void drawCircle(Color color, float x, float y, float r){
        renderer.setColor(color);
        renderer.circle(x, y, r);
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
