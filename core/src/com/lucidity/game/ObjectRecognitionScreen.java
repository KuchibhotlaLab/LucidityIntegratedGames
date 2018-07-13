package com.lucidity.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;

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
    boolean difficult;
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


    public ObjectRecognitionScreen(ObjectRecognitionGame game, boolean difficulty) {
        this.difficult = difficulty;
        this.game = game;
        hasColor = difficult;

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
        shapes.add(new ArrayList<Integer>(Arrays.asList(screenWidth/2,screenHeight/2, screenWidth/3)));
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

        if(elapsed < 2){

            batch.begin();
            font.getData().setScale(GameThreeConstants.PROMPT_SCALE);

            final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.PROMPT_THREE);
            font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width)/2, screenHeight * 2 / 3);

            if(difficult) {
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

        } else if (elapsed > 2 && elapsed < 4) {


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

        } else if(elapsed > 4 && elapsed < 6){
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

            if(difficult){
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
            disabled = false;
            renderer.begin(ShapeRenderer.ShapeType.Filled);
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



            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(GameThreeConstants.HARD_COLOR);
            renderer.rect(same.x, same.y, same.getWidth(), same.getHeight());
            renderer.rect(diff.x, diff.y, diff.getWidth(), diff.getHeight());
            renderer.end();


            batch.begin();
            font.getData().setScale(GameThreeConstants.ANSWER_SCALE);
            //prints text on back button
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

            if(sameSelected || diffSelected) {
                if (isCorrect()) {
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameThreeConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 10);
                } else {
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width) / 2, screenHeight / 10);
                }
            }

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

            if(!delayOn && (sameSelected || diffSelected)){
                delayOn = true;
                delayed = elapsed;
            }

            if(elapsed - delayed >= 1f && delayOn) {
                if(isCorrect()){
                    ++score;
                }
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
        disabled = true;
        sSame = false;
        cSame = false;
        sameSelected = false;
        diffSelected = false;
        onEnd = false;
        onBack = false;
        delayOn = false;

        int sAnswer = (int)(Math.random() * shapes.size());
        int sShow = (int)(Math.random() * shapes.size());

        sSame = (sAnswer == sShow);

        sCorrect = shapes.get(sAnswer);
        sShown = shapes.get(sShow);

        if(hasColor){
            int cAnswer = (int)(Math.random() * colors.size());
            int cShow = (int)(Math.random() * colors.size());
            cSame = (cAnswer == cShow);
            cCorrect = colors.get(cAnswer);
            cShown = colors.get(cShow);
        } else {
            cCorrect = Color.LIGHT_GRAY;
            cShown = Color.LIGHT_GRAY;
        }
    }

    private boolean isCorrect(){
        if(!difficult){
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
}
