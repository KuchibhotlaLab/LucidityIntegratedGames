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
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;
import java.util.ArrayList;

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

    private ShapeRenderer renderer;

    String username;
    ArrayList<String> imgNames;
    ArrayList<ArrayList<String>> imgTags;
    ArrayList<String> imgGenders;
    int gameMode;

    Rectangle answer1, answer2, end, back;
    boolean onSelect1, onSelect2 = false;
    boolean onEnd, onBack = false;
    String attr1, attr2;
    String correct;


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

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        answer1 = new Rectangle();
        answer2 = new Rectangle();
        answer1.height = answer2.height = screenHeight / 12;
        answer1.width = answer2.width = screenWidth / 2;
        answer1.x = answer2.x = screenWidth / 4;
        answer1.y = screenHeight / 10;
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
        imgGenders = game.getPicturegenders();
        username = game.getUsername();

        timerStart = true;
        trialTime = new double[5];
        trialSuccess = new int[5];


        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        //generateTrial();
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
        Gdx.gl.glClearColor(FacialGameConstants.BACKGROUND_COLOR.r, FacialGameConstants.BACKGROUND_COLOR.g, FacialGameConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;

        if(elapsed < 2) {
            batch.begin();
            font.getData().setScale(FacialGameConstants.PROMPT_SCALE);

            if (trial == 1) {
                final GlyphLayout promptLayout_two = new GlyphLayout(font, RecallGameConstants.PROMPT_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2, screenHeight / 2);

                final GlyphLayout promptLayout_one = new GlyphLayout(font, RecallGameConstants.PROMPT_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width) / 2,
                        screenHeight / 2 + 1.5f * promptLayout_two.height);
            } else {
                //TODO: finish implementing game
                //Start timer
                if (timerStart){
                    trialStartTime = TimeUtils.nanoTime();
                    timerStart = false;
                    disableTouchDown = false;
                }

                renderer.begin(ShapeRenderer.ShapeType.Filled);

                if(!onSelect1){
                    renderer.setColor(FacialGameConstants.W2F_COLOR);
                } else {
                    renderer.setColor(FacialGameConstants.CHOICE_COLOR);
                }
                renderer.rect(answer1.x, answer1.y, answer1.getWidth(), answer1.getHeight());


                if(!onSelect2){
                    renderer.setColor(FacialGameConstants.W2F_COLOR);
                    //System.out.println("you should be in this loop 2 ");
                } else {
                    //System.out.println("are you even here 1 ");
                    renderer.setColor(FacialGameConstants.CHOICE_COLOR);
                }
                renderer.rect(answer2.x, answer2.y, answer2.getWidth(), answer2.getHeight());


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
            }

            batch.end();

        } else {
            //Start timer
            if (timerStart) {
                trialStartTime = TimeUtils.nanoTime();
                timerStart = false;
                disableTouchDown = false;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
