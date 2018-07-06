package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class FaceToNameScreen extends InputAdapter implements Screen {
    private FacialMemoryGame game;

    ExtendViewport viewport;
    ScreenViewport hudViewport;

    int screenWidth, screenHeight;
    int score, trial;

    ShapeRenderer renderer;

    Rectangle answer1, answer2, end, back;
    boolean onSelect1, onSelect2 = false;
    boolean onEnd, onBack = false;
    String name1, name2;
    String correct;

    Texture face;
    SpriteBatch batch;
    BitmapFont font;

    float elapsed;

    public FaceToNameScreen(FacialMemoryGame game, int points, int trials) {
        this.game = game;
        answer1 = answer2 = new Rectangle();
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


        File folder = new File("/");
        File[] listOfFiles = folder.listFiles();

        //face = new Texture(Gdx.files.external(listOfFiles[0].getPath()));

        face = new Texture(Gdx.files.internal("test.jpg"));
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
            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if(!onSelect1){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);

                if(correct.equals(name1)){
                    batch.begin();
                    font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                    batch.end();
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           ++score;
                                           ++trial;
                                           game.setScreen(new FaceToNameScreen(game, score, trial));
                                       }
                                   },
                            30/30.0f);
                } else {
                    batch.begin();
                    font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                    batch.end();
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           ++trial;
                                           game.setScreen(new FaceToNameScreen(game, score, trial));
                                       }
                                   },
                            30/30.0f);
                }
            }
            renderer.rect(answer1.x, answer1.y, answer1.getWidth(), answer1.getHeight());


            if(!onSelect2){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);

                if(correct.equals(name2)){
                    batch.begin();
                    font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                    batch.end();
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           ++score;
                                           ++trial;
                                           game.setScreen(new FaceToNameScreen(game, score, trial));
                                       }
                                   },
                            30/30.0f);
                } else {
                    batch.begin();
                    font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                    final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                    font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                    batch.end();
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           ++trial;
                                           game.setScreen(new FaceToNameScreen(game, score, trial));
                                       }
                                   },
                            30/30.0f);
                }

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
                        30/30.0f);
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
                        30/30.0f);
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


            batch.draw(face, (screenWidth - face.getWidth())/2, (screenHeight - face.getHeight())/2);
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
        if(answer1.contains(screenX, screenHeight - screenY)){
            onSelect1 = !onSelect1;
            onSelect2 = false;
        }
        if(answer2.contains(screenX, screenHeight - screenY)){
            onSelect2 = !onSelect2;
            onSelect1 = false;
        }

        if(end.contains(screenX, screenHeight - screenY)){
            onEnd = true;
        }

        if(back.contains(screenX, screenHeight - screenY)){
            onBack = true;
        }

        return true;
    }

    private void generateTrial(){
        int position = (int) (Math.random() * 2);
        System.out.println(position);
        if(position == 0) {
            name1 = "Correct Answer";
            name2 = "Jane Doe";
        } else {
            name1 = "Jane Doe";
            name2 = "Correct Answer";
        }

        correct = "Correct Answer";
    }

}
