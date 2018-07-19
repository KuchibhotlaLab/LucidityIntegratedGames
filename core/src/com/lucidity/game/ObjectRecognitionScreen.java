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
        shapes.add(new ArrayList<Integer>(Arrays.asList(screenWidth / 2,screenHeight * 2 / 3, screenWidth/3)));
        shapes.add(new ArrayList<Integer>(Arrays.asList(screenWidth/6, screenHeight/3, screenWidth/2, screenHeight * 2 / 3, screenWidth * 5 / 6, screenHeight/3)));


        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);
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
        Gdx.gl.glClearColor(GameThreeConstants.BACKGROUND_COLOR.r, GameThreeConstants.BACKGROUND_COLOR.g, GameThreeConstants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;

        if(elapsed <= 2){

            batch.begin();
            font.getData().setScale(GameThreeConstants.PROMPT_SCALE);

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

            final GlyphLayout promptLayout_four = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_FOUR);
            font.draw(batch, promptLayout_four, (screenWidth - promptLayout_four.width)/2, screenHeight * 2 / 3);

            final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_THREE);
            font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width)/2,
                    screenHeight * 2 / 3 + 1.5f * promptLayout_four.height);

            final GlyphLayout promptLayout_two = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_TWO);
            font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width)/2,
                    screenHeight * 2 / 3 + 1.5f * promptLayout_three.height + 1.5f * promptLayout_four.height);

            if(difficult == 1 || difficult == 2){
                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_ONE_TWO);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height + 1.5f * promptLayout_four.height);
            } else {
                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.INSTRUCTION_ONE_ONE);
                font.draw(batch, promptLayout_one, (screenWidth - promptLayout_one.width)/2,
                        screenHeight * 2 / 3 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height + 1.5f * promptLayout_four.height);
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

            if(difficult == 0 || difficult == 1){
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



                if(!sameSelected){
                    renderer.setColor(GameThreeConstants.TITLE_COLOR);
                } else {
                    renderer.setColor(GameThreeConstants.EASY_COLOR);
                }
                renderer.rect(same.x, same.y, same.getWidth(), same.getHeight());


                if(!diffSelected){
                    renderer.setColor(GameThreeConstants.TITLE_COLOR);
                    //System.out.println("you should be in this loop 2 ");
                } else {
                    //System.out.println("are you even here 1 ");
                    renderer.setColor(GameThreeConstants.EASY_COLOR);
                }
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
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight * 3 / 4);
                        final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.CORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight * 3 / 4 + 1.5f * reactionLayout.height);

                    } else {
                        font.setColor(GameOneConstants.INCORRECT_COLOR);
                        final GlyphLayout reactionLayout = new GlyphLayout(font, GameThreeConstants.REACTION_TIME_PROMPT + Math.round(trialTime[trial - 1] * 100.0) / 100.0 + " seconds!");
                        font.draw(batch, reactionLayout, (screenWidth - reactionLayout.width) / 2, screenHeight * 3 / 4);
                        final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                        font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight * 3 / 4  + 1.5f * reactionLayout.height);

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

                ArrayList<Integer> sShowOne, sShowTwo;
                sShowOne = new ArrayList<Integer>();
                sShowTwo = new ArrayList<Integer>();
                Color cShowOne, cShowTwo;

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

                renderer.setColor(cShowOne);
                switch (sShowOne.size()) {
                    case 3:
                        renderer.circle(sShowOne.get(0) / 2, sShowOne.get(1)/2 + screenHeight/8, sShowOne.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect(sShowOne.get(0) / 2, sShowOne.get(1), sShowOne.get(2) / 2, sShowOne.get(3) / 2);
                        break;
                    case 6:
                        renderer.triangle(sShowOne.get(0) / 2, sShowOne.get(1)/2 + screenHeight / 6, sShowOne.get(2)/ 2, sShowOne.get(3) / 2 + screenHeight / 6, sShowOne.get(4) / 2, sShowOne.get(5) / 2  + screenHeight / 6);
                        break;
                    default:
                        break;
                }

                renderer.setColor(cShowTwo);
                switch (sShowOne.size()) {
                    case 3:
                        renderer.circle(sShowOne.get(0) / 2 + screenWidth / 2, sShowOne.get(1) / 2 + screenHeight/8, sShowOne.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect(sShowOne.get(0) / 2 + screenWidth / 2 , sShowOne.get(1), sShowOne.get(2) / 2, sShowOne.get(3) / 2);
                        break;
                    case 6:
                        renderer.triangle(sShowOne.get(0) / 2 + screenWidth  / 2, sShowOne.get(1) / 2  + screenHeight / 6, sShowOne.get(2)/2 + screenWidth /2, sShowOne.get(3) / 2  + screenHeight / 6, sShowOne.get(4)/2 +  screenWidth /2, sShowOne.get(5)/2  + screenHeight / 6);
                        break;
                    default:
                        break;
                }

                renderer.setColor(cShowTwo);
                switch (sShowTwo.size()) {
                    case 3:
                        renderer.circle(sShowTwo.get(0) / 2, sShowTwo.get(1) + screenHeight / 8, sShowTwo.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect(sShowTwo.get(0) / 2, sShowTwo.get(1) + screenHeight / 3, sShowTwo.get(2) / 2, sShowTwo.get(3) / 2);
                        break;
                    case 6:
                        renderer.triangle(sShowTwo.get(0) / 2, sShowTwo.get(1) / 2 + screenHeight / 2, sShowTwo.get(2)/2, sShowTwo.get(3) / 2 + screenHeight / 2, sShowTwo.get(4)/2, sShowTwo.get(5)/2 + screenHeight / 2);
                        break;
                    default:
                        break;
                }

                renderer.setColor(cShowOne);
                switch (sShowTwo.size()) {
                    case 3:
                        renderer.circle(sShowTwo.get(0) / 2 + screenWidth / 2, sShowTwo.get(1) + screenHeight / 8, sShowTwo.get(2) / 2);
                        break;
                    case 4:
                        renderer.rect(sShowTwo.get(0) / 2 + screenWidth / 2, sShowTwo.get(1) + screenHeight / 3, sShowTwo.get(2) / 2, sShowTwo.get(3) / 2);
                        break;
                    case 6:
                        renderer.triangle(sShowTwo.get(0) / 2 + screenWidth /2, sShowTwo.get(1) / 2 + screenHeight / 2, sShowTwo.get(2)/2 + screenWidth /2, sShowTwo.get(3) / 2 + screenHeight / 2, sShowTwo.get(4)/2 + screenWidth /2, sShowTwo.get(5)/2 + screenHeight / 2);
                        break;
                    default:
                        break;
                }

                renderer.end();

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
                                   public void run() {game.setScreen(new ObjectRecognitionDifficultyScreen(game));
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

            font.draw(batch, GameTwoConstants.SCORE_LABEL + Integer.toString(score),
                    GameTwoConstants.SCORE_CENTER, screenHeight - GameTwoConstants.SCORE_CENTER);

            final GlyphLayout layout_scores = new GlyphLayout(font, GameTwoConstants.SCORE_LABEL);

            font.draw(batch, GameTwoConstants.TRIAL_LABEL + Integer.toString(trial),
                    GameTwoConstants.SCORE_CENTER,
                    screenHeight - GameTwoConstants.SCORE_CENTER - layout_scores.height * 1.5f);
            batch.end();


            if(elapsed - delayed >= 1f && delayOn) {
                if(isCorrect()){
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
            if (same.contains(screenX, screenHeight - screenY)) {
                sameSelected = !sameSelected;
                diffSelected = false;
            }
            if (diff.contains(screenX, screenHeight - screenY)) {
                diffSelected = !diffSelected;
                sameSelected = false;
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
        int sShow = (int)(Math.random() * shapes.size());

        sSame = (sAnswer == sShow);

        sCorrect = shapes.get(sAnswer);

        if(difficult == 2) {
            while (sShow == sAnswer) {
                sShow = (int)(Math.random() * shapes.size());
            }
        }
        sShown = shapes.get(sShow);

        if(hasColor){
            int cAnswer = (int)(Math.random() * colors.size());
            int cShow = (int)(Math.random() * colors.size());
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

    }

    private boolean isCorrect(){
        if(difficult == 0){
            return (sameSelected && sCorrect.equals(sShown)) || (diffSelected && !sCorrect.equals(sShown));
        } else {
            if(sameSelected){
                return (sCorrect.equals(sShown) && cCorrect.equals(cShown));
            } else if (diffSelected){
                return (!sCorrect.equals(sShown) || !cCorrect.equals(cShown));
            }
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
