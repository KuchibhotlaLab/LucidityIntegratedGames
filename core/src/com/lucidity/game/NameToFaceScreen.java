package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
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
import java.util.logging.FileHandler;

/**
 * Created by lixiaoyan on 7/5/18.
 */

public class NameToFaceScreen extends InputAdapter implements Screen {
    private FacialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth, screenHeight;
    int score, trial;

    ShapeRenderer renderer;

    Texture faceOne, faceTwo;
    Sprite spriteOne, spriteTwo;
    SpriteBatch batch;
    BitmapFont font;

    Rectangle answerOne, answerTwo, end, back;
    String answerOneName, answerTwoName;
    boolean onEnd, onBack = false;
    boolean selectedOne, selectedTwo = false;
    ArrayList<File> validFiles;
    //ArrayList<FileHandle> validFiles;

    String tag;
    String attribute;
    String correctAnswer;

    String username;
    ArrayList<String> imgNames;
    ArrayList<ArrayList<String>> imgTags;

    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    float elapsed = 0;
    //cheap fix
    boolean delayOn= false;
    float delayed = -10000;
    int gameMode;

    private boolean disableTouchDown=true;

    public NameToFaceScreen(FacialMemoryGame game, int points, int trials, int mode) {
        this.game = game;
        score = points;
        trial = trials;

        gameMode = mode;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);

        answerOne = new Rectangle();
        answerTwo = new Rectangle();

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;


        imgNames = game.getPicturenames();
        imgTags = game.getPicturetags();
        username = game.getUsername();

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];
        //data/data/com.lucidity.game/app_imageDir/username
        //data/user/0/com.lucidity.game/app_imageDir/Coco
        //data/user/0/com.lucidity.game/files -> local storage path
        //Gdx.file.local()
        /*System.out.println(Gdx.files.getLocalStoragePath());
        String locRoot = "../app_imageDir/" + username;
        FileHandle [] listOfFiles;
        listOfFiles=Gdx.files.internal(locRoot).list();
        for(FileHandle file : listOfFiles){
            System.out.println(file.name());
            if("jpg".equals(file.extension()) || "png".equals(file.extension())){
                validFiles.add(file);
            }
        }*/

        /*String locRoot = Gdx.files.getLocalStoragePath();*/

        //TODO: create interface for this
        String locRoot = "data/user/0/com.lucidity.game/app_imageDir/" + username;
        File folder = new File(locRoot);
        File[] listOfFiles = folder.listFiles();
        validFiles = new ArrayList<File>();
        for(File file : listOfFiles){
            if(file.isFile()){
                System.out.println(file.getPath());
                if("jpg".equals(file.getName().substring(file.getName().length()-3, file.getName().length())) ||
                        "png".equals(file.getName().substring(file.getName().length()-3, file.getName().length()))){
                    validFiles.add(file);
                }
            }
        }

        generateTrial();

    }
    @Override
    public void show() {
        viewport = new ExtendViewport(GameTwoConstants.WORLD_SIZE, GameTwoConstants.WORLD_SIZE);
        hudViewport = new ScreenViewport();

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        viewport.apply(true);
        Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;


        if(elapsed < 2) {

            batch.begin();
            font.getData().setScale(GameTwoConstants.PROMPT_SCALE);

            final GlyphLayout promptLayout_three = new GlyphLayout(font, GameTwoConstants.PROMPT_FOUR);
            font.draw(batch, promptLayout_three, (screenWidth- promptLayout_three.width) / 2,
                    screenHeight/2);

            final GlyphLayout promptLayout_two = new GlyphLayout(font, GameTwoConstants.PROMPT_THREE + attribute);
            font.draw(batch, promptLayout_two, (screenWidth- promptLayout_two.width) / 2,
                    screenHeight/2 + 1.5f * promptLayout_three.height);


            final GlyphLayout promptLayout_one = new GlyphLayout(font, GameTwoConstants.PROMPT_ONE);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                    screenHeight/2 + 1.5f * promptLayout_three.height + 1.5f * promptLayout_two.height);

            batch.end();

        } else {
            //Start timer
            if (timerStart){
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if(!onEnd){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               game.setScreen(new EndScreen(game, score, trial));
                           }
                       },
                30/30.0f);
            }

            renderer.rect(end.x, end.y, end.width, end.height);


            if(!onBack){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       game.setScreen(new ModeScreen(game));
                                   }
                               },
                        30/30.0f);
            }
            renderer.rect(back.x, back.y, back.width, back.height);


            if(!selectedOne){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                if(!delayOn){
                    delayOn = true;
                    delayed = elapsed;

                    //record reaction time here
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                    }
                }

            }
            renderer.rect(answerOne.x-20, answerOne.y-20, answerOne.width+40, answerOne.height+40);


            if(!selectedTwo){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                if(!delayOn){
                    delayOn = true;
                    delayed = elapsed;

                    //record reaction time here
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                    }
                }
            }
            renderer.rect(answerTwo.x - 20, answerTwo.y - 20, answerTwo.width+40, answerTwo.height+40);
            renderer.end();

            batch.begin();
            font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
            if(selectedOne){
                disableTouchDown = true;
                if(correctAnswer.equals(answerOneName)){
                    font.setColor(GameOneConstants.CORRECT_COLOR);
                    final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                    font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 8);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 8 + 1.5f * reactionLayout.height);

                } else {
                    font.setColor(GameOneConstants.INCORRECT_COLOR);
                    final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                    font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 8);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 8 + 1.5f * reactionLayout.height);
                }
            } else if (selectedTwo) {
                disableTouchDown = true;
                if(correctAnswer.equals(answerTwoName)){
                    font.setColor(GameOneConstants.CORRECT_COLOR);
                    final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                    font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 8);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 8 + 1.5f * reactionLayout.height);

                } else {
                    font.setColor(GameOneConstants.INCORRECT_COLOR);
                    final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                    font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight / 8);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 8 + 1.5f * reactionLayout.height);

                }
            }
            batch.end();

            if(elapsed - delayed >= 1f && delayOn) {
                if(selectedOne && correctAnswer.equals(answerOneName) ||
                        selectedTwo && correctAnswer.equals(answerTwoName)) {
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

            renderer.begin(ShapeRenderer.ShapeType.Line);

            renderer.setColor(GameTwoConstants.OUTLINE_COLOR);
            renderer.rect(answerOne.x-20, answerOne.y-20, answerOne.width+40, answerOne.height+40);
            renderer.rect(answerTwo.x - 20, answerTwo.y - 20, answerTwo.width+40, answerTwo.height+40);

            renderer.rect(end.x, end.y, end.width, end.height);
            renderer.rect(back.x, back.y, back.width, back.height);

            renderer.end();

            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(GameTwoConstants.PROMPT_SCALE);
            final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.PROMPT + tag);
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight * 7 / 8);


            font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
            font.draw(batch, GameTwoConstants.SCORE_LABEL + Integer.toString(score),
                    GameTwoConstants.SCORE_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, GameTwoConstants.SCORE_LABEL);

            font.draw(batch, GameTwoConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameTwoConstants.SCORE_CENTER,
                    screenHeight - GameTwoConstants.SCORE_CENTER - layout_scores.height * 1.5f);

            //prints text on submit button
            font.draw(batch, GameTwoConstants.BACK_TEXT,
                    (int) (back.x + 0.2 * back.getWidth()),
                    (int) (back.y + 0.6 * back.getHeight()));


            //prints text on end button
            font.draw(batch, GameTwoConstants.END_TEXT,
                    (int) (end.x + 0.25 * end.getWidth()),
                    (int) (end.y + 0.6 * end.getHeight()));


            //batch.draw(sprite, (screenWidth - sprite.getWidth())/2, (screenHeight - sprite.getHeight())/2);
            batch.draw(spriteOne, answerOne.x, answerOne.y, answerOne.width, answerOne.height);
            batch.draw(spriteTwo, answerTwo.x, answerTwo.y, answerTwo.width, answerTwo.height);

            //sprite.setPosition((screenWidth - face.getWidth())/2, (screenHeight - face.getHeight())/2,);
            //sprite.draw(batch);
            batch.end();
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
            if (answerOne.contains(screenX, screenHeight - screenY)) {
                selectedOne = true;
            }

            if (answerTwo.contains(screenX, screenHeight - screenY)) {
                selectedTwo = true;
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

    private void generateTrial() {
        elapsed = 0;
        delayed = -10000;
        tag = "";
        if(gameMode == GameTwoConstants.MODE_NAME){
            attribute = "name";
        } else if(gameMode == GameTwoConstants.MODE_ATTR){
            attribute = "relation";
        }

        delayOn = false;
        selectedTwo = false;
        selectedOne = false;
        timerStart = true;

        int position = (int) (Math.random() * validFiles.size());
        int correct = (int) (Math.random() * 2);

        /*FileHandle file = validFiles.get(position);
        validFiles.remove(file);

        if (correct == 0) {
            faceOne = new Texture(file);
            faceTwo = new Texture(validFiles.get((int) (Math.random() * validFiles.size())));
        } else {

            faceTwo = new Texture(file);
            faceOne = new Texture(validFiles.get((int) (Math.random() * validFiles.size())));
        }

        validFiles.add(file);*/

        File file = validFiles.get(position);
        if(gameMode == GameTwoConstants.MODE_NAME){
            tag = imgTags.get(imgNames.indexOf(file.getName())).get(0);
        } else if(gameMode == GameTwoConstants.MODE_ATTR){
            tag = imgTags.get(imgNames.indexOf(file.getName())).get(1);
        }


        correctAnswer = file.getName();

        validFiles.remove(file);
        int incorrect = (int) (Math.random() * validFiles.size());
        if (correct == 0) {
            faceOne = new Texture(Gdx.files.absolute(file.getPath()));
            answerOneName = file.getName();
            faceTwo = new Texture(Gdx.files.absolute(validFiles.get(incorrect).getPath()));
            answerTwoName = validFiles.get(incorrect).getName();
        } else {

            faceTwo = new Texture(Gdx.files.absolute(file.getPath()));
            answerTwoName = file.getName();
            faceOne = new Texture(Gdx.files.absolute(validFiles.get(incorrect).getPath()));
            answerOneName = validFiles.get(incorrect).getName();
        }

        validFiles.add(file);

        spriteOne = new Sprite(faceOne);
        spriteTwo = new Sprite(faceTwo);

        int interval = screenWidth/10;

        answerOne.width = answerTwo.width = (screenWidth - interval) / 2;
        answerOne.height = answerTwo.height = screenHeight * 3 / 8;
        answerOne.y = answerTwo.y = screenHeight / 3;
        answerOne.x = interval/4;

        answerTwo.x = answerOne.getWidth() + interval*3/4;
    }

    //Posts score and stats to MySQL database
    private void postScore(){
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
    }
}
