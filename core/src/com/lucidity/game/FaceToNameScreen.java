package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class

FaceToNameScreen extends InputAdapter implements Screen {
    private FacialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth, screenHeight;
    int score, trial;

    ShapeRenderer renderer;

    String username;
    ArrayList<String> imgNames;
    ArrayList<ArrayList<String>> imgTags;

    Rectangle answer1, answer2, end, back;
    boolean onSelect1, onSelect2 = false;
    boolean onEnd, onBack = false;
    String name1, name2;
    String correct;


    ArrayList<File> validFiles;
    Texture face;
    Sprite display;
    SpriteBatch batch;
    BitmapFont font;

    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    float elapsed = 0;
    //cheap fix
    //TODO: figure out how to properly time
    boolean delayOn= false;
    float delayed = -10000;

    private boolean disableTouchDown=true;

    public FaceToNameScreen(FacialMemoryGame game, int points, int trials) {
        this.game = game;
        score = points;
        trial = trials;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        answer1 = new Rectangle();
        answer2 = new Rectangle();
        answer1.height = answer2.height = screenHeight / 12;
        answer1.width = answer2.width = screenWidth / 2;
        answer1.x = answer2.x = screenWidth / 4;
        answer1.y = screenHeight / 8;
        answer2.y = answer1.y + answer1.height;

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

        String locRoot = "data/user/0/com.lucidity.game/app_imageDir/" + username;
        File folder = new File(locRoot);
        System.out.println(locRoot);
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

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);

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
            final GlyphLayout promptLayout_two = new GlyphLayout(font, GameTwoConstants.PROMPT_TWO);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2, screenHeight / 2);

            final GlyphLayout promptLayout_one = new GlyphLayout(font, GameTwoConstants.PROMPT_ONE);
            font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                    screenHeight / 2 + 1.5f * promptLayout_two.height);

            batch.end();

        } else {
            //Start timer
            if (timerStart){
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if(!onSelect1){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
            }
            renderer.rect(answer1.x, answer1.y, answer1.getWidth(), answer1.getHeight());


            if(!onSelect2){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
                //System.out.println("you should be in this loop 2 ");
            } else {
                //System.out.println("are you even here 1 ");
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
            }
            renderer.rect(answer2.x, answer2.y, answer2.getWidth(), answer2.getHeight());


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
                        1);
            }

            renderer.rect(end.x, end.y, end.width, end.height);


            if(!onBack){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
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

            renderer.setColor(GameTwoConstants.OUTLINE_COLOR);
            renderer.rect(answer1.x, answer1.y, answer1.getWidth(), answer1.getHeight());
            renderer.rect(answer2.x, answer2.y, answer2.getWidth(), answer2.getHeight());

            renderer.rect(end.x, end.y, end.width, end.height);
            renderer.rect(back.x, back.y, back.width, back.height);

            renderer.end();

            batch.begin();
            font.getData().setScale(GameTwoConstants.ANSWER_SCALE);

            if(onSelect1){
                disableTouchDown = true;
                if(correct.equals(name1)){
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                } else {
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                }
            } else if (onSelect2) {
                disableTouchDown = true;
                if(correct.equals(name2)){
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                } else {
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                }
            }

            batch.end();

            batch.begin();
            font.getData().setScale(GameTwoConstants.PROMPT_SCALE);
            final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.PROMPT + "this?");
            font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight * 7 / 8);


            font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
            final GlyphLayout layout_two = new GlyphLayout(font, name2);
            final float fontX_two = (screenWidth - layout_two.width) / 2;
            final float fontY_two = (answer2.height * 0.6f + answer2.y);
            font.draw(batch, layout_two, fontX_two, fontY_two);

            final GlyphLayout layout_one = new GlyphLayout(font, name1);
            final float fontX_one = (screenWidth - layout_one.width) / 2;
            final float fontY_one = (answer1.height * 0.6f + answer1.y);
            font.draw(batch, layout_one, fontX_one, fontY_one);


            //prints text on submit button
            font.draw(batch, GameTwoConstants.BACK_TEXT,
                    (int) (back.x + 0.25 * back.getWidth()),
                    (int) (back.y + 0.6 * back.getHeight()));




            //prints text on end button
            font.draw(batch, GameTwoConstants.END_TEXT,
                    (int) (end.x + 0.3 * end.getWidth()),
                    (int) (end.y + 0.6 * end.getHeight()));


            font.draw(batch, GameTwoConstants.SCORE_LABEL + Integer.toString(score),
                    GameTwoConstants.SCORE_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, GameTwoConstants.SCORE_LABEL);

            font.draw(batch, GameTwoConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameTwoConstants.SCORE_CENTER,
                    screenHeight - GameTwoConstants.SCORE_CENTER - layout_scores.height * 1.5f);


            //batch.draw(face, (screenWidth - face.getWidth())/2, (screenHeight - face.getHeight())/2);
            batch.draw(display, screenWidth/5, screenHeight/3, screenWidth * 3 / 5, screenHeight * 5/ 12);

            batch.end();



            if(!delayOn && (onSelect2 || onSelect1)){
                delayOn = true;
                delayed = elapsed;
            }

            if(elapsed - delayed >= 1f && delayOn) {
                System.out.println("calls new trial");

                if(onSelect1 && correct.equals(name1) ||
                        onSelect2 && correct.equals(name2)) {
                    ++score;

                    //Save time in seconds
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
                        trialSuccess[trial - 1] = 1;
                    }
                } else {
                    //Save time in seconds
                    if(trial <= 5) {
                        trialTime[trial - 1] = (TimeUtils.nanoTime() - trialStartTime) / 1000000000.0;
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
            if (answer1.contains(screenX, screenHeight - screenY)) {
                onSelect1 = !onSelect1;
                onSelect2 = false;
            }
            if (answer2.contains(screenX, screenHeight - screenY)) {
                onSelect2 = !onSelect2;
                onSelect1 = false;
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
        name1 = "";
        name2 = "";
        elapsed = 0;
        delayed = -10000;
        delayOn = false;

        onSelect1 = false;
        onSelect2 = false;
        onEnd = false;
        onBack = false;
        timerStart = true;

        int picture = (int) (Math.random() * validFiles.size());
        face = new Texture(Gdx.files.absolute(validFiles.get(picture).getPath()));
        display = new Sprite(face);
        /*face = new Texture(Gdx.files.internal("test.jpg"));*/

        correct = imgTags.get(imgNames.indexOf(validFiles.get(picture).getName())).get(0);
        int incorrect = (int) (Math.random() * validFiles.size());
        while (incorrect == picture) {
            incorrect = (int) (Math.random() * validFiles.size());
        }

        int position = (int) (Math.random() * 2);
        if(position == 0) {
            name1 = correct;
            name2 = imgTags.get(imgNames.indexOf(validFiles.get(incorrect).getName())).get(0);;
        } else {
            name1 = imgTags.get(imgNames.indexOf(validFiles.get(incorrect).getName())).get(0);;
            name2 = correct;
        }


    }

    //Posts score and stats to MySQL database
    private void postScore(){
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl("http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_facetonamegame_score.php");

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
