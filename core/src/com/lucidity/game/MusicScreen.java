package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpParametersUtils;
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
    private int score, trial;
    private int maxTrial = 5;

    private ShapeRenderer renderer;

    Sprite display;
    SpriteBatch batch;
    BitmapFont font;

    Texture musicBtn, homeBut, returnBut;
    Rectangle end, back, play;
    boolean onEnd, onBack, onplay= false;

    private boolean timerStart;
    private long trialStartTime;
    private int[] trialSuccess;
    private double[] trialTime;

    float elapsed = 0;
    boolean delayOn= false;
    float delayed = -10000;

    private boolean disableTouchDown=true;
    String username;
    Music music;
    ArrayList<Music> validSongs;

    String name, author;


    public MusicScreen(MusicGame game) {
        this.game = game;
        username = game.getUsername();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);

        musicBtn = new Texture(Gdx.files.internal("data/musicBtn.png"));
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
        play.y = screenHeight * 2 / 3;

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
        elapsed += delta;
        batch.begin();
        triggerEnd();
        triggerBack();
        triggerPlaySong();
        batch.end();
        /*if(elapsed < 2) {
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
            triggerEnd();
            triggerBack();
            triggerPlaySong();
            batch.end();
        }*/
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
        if(!disableTouchDown) {
            if(back.contains(screenX, screenHeight - screenY)){
                onBack = true;
            }

            if(end.contains(screenX, screenHeight - screenY)){
                onEnd = true;
            }

            if(play.contains(screenX, screenHeight - screenY)){
                onplay = !onplay;
                System.out.println("playing status: " + onplay);
            }
        }
        return true;
    }

    private void generateTrial(){
        music = Gdx.audio.newMusic(Gdx.files.absolute("data/user/0/com.lucidity.game/app_audioDir/Observent.mp3"));
        //music.play();
        name = "Observent";
        author = "Florent Mothe";
        trial++;

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
            /*Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {game.setScreen(new DifficultyScreen(game));
                               }
                           },
                    1);*/
        }
        batch.draw(returnBut, end.x, end.y, end.width, end.height);

    }

    private void triggerPlaySong(){
        if(onplay){
            music.stop();
            onplay = false;
        } else {
            music.play();
            onplay = true;
        }
        batch.draw(musicBtn, play.x, play.y, play.width, play.height);

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
