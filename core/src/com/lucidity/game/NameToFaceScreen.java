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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;
import java.util.ArrayList;

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
    boolean onEnd, onBack = false;
    boolean selectedOne, selectedTwo = false;
    ArrayList<File> validFiles;

    String name;
    String attribute;

    float elapsed = 0;

    public NameToFaceScreen(FacialMemoryGame game, int points, int trials) {
        this.game = game;
        score = points;
        trial = trials;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular.fnt"), false);

        answerOne = new Rectangle();
        answerTwo = new Rectangle();

        end = new Rectangle();
        back = new Rectangle();
        end.height = back.height = screenHeight / 16;
        end.width = back.width = screenWidth / 5;
        end.y = back.y = screenHeight - end.height - 25;
        end.x = screenWidth / 2;
        back.x = screenWidth * 3 / 4;


        String locRoot = Gdx.files.getLocalStoragePath();
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

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if(!onEnd){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       elapsed = 0;
                                       generateTrial();
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
                                       elapsed = 0;
                                       generateTrial();
                                   }
                               },
                        30/30.0f);
            }
            renderer.rect(back.x, back.y, back.width, back.height);


            if(!selectedOne){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                batch.begin();
                font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.CORRECT_MESSAGE);
                font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                batch.end();
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {

                                       //TODO: check correctness once can get string
                                       game.setScreen(new NameToFaceScreen(game, score, trial));
                                   }
                               },
                        30/30.0f);
            }
            renderer.rect(answerOne.x-20, answerOne.y-20, answerOne.width+40, answerOne.height+40);


            if(!selectedTwo){
                renderer.setColor(GameTwoConstants.W2F_COLOR);
            } else {
                renderer.setColor(GameTwoConstants.CHOICE_COLOR);
                batch.begin();
                font.getData().setScale(GameTwoConstants.ANSWER_SCALE);
                final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.INCORRECT_MESSAGE);
                font.draw(batch, promptLayout, (screenWidth - promptLayout.width)/2, screenHeight / 10);
                batch.end();
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       game.setScreen(new NameToFaceScreen(game, score, trial));
                                   }
                               },
                        30/30.0f);
            }
            renderer.rect(answerTwo.x - 10, answerTwo.y - 20, answerTwo.width+30, answerTwo.height+40);


            renderer.end();

            renderer.begin(ShapeRenderer.ShapeType.Line);

            renderer.setColor(GameTwoConstants.OUTLINE_COLOR);
            renderer.rect(answerOne.x-20, answerOne.y-20, answerOne.width+30, answerOne.height+40);
            renderer.rect(answerTwo.x - 10, answerTwo.y - 20, answerTwo.width+30, answerTwo.height+40);

            renderer.rect(end.x, end.y, end.width, end.height);
            renderer.rect(back.x, back.y, back.width, back.height);

            renderer.end();



            batch.begin();
            font.getData().setScale(GameTwoConstants.PROMPT_SCALE);
            final GlyphLayout promptLayout = new GlyphLayout(font, GameTwoConstants.PROMPT + name);
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
                    (int) (back.x + 0.25 * back.getWidth()),
                    (int) (back.y + 0.6 * back.getHeight()));




            //prints text on end button
            font.draw(batch, GameTwoConstants.END_TEXT,
                    (int) (end.x + 0.3 * end.getWidth()),
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
        if (answerOne.contains(screenX, screenHeight - screenY)) {
            selectedOne = true;
        }

        if(answerTwo.contains(screenX, screenHeight - screenY)) {
            selectedTwo = true;
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
        attribute = "name";
        answerOne = new Rectangle();
        answerTwo = new Rectangle();


        //instead of get name first, get name after get file
        //TODO: get correct name
        name = "Jane Doe";
        int position = (int)(Math.random()*validFiles.size());
        int correct = (int)(Math.random() * 2);
        File file = validFiles.get(position);
        validFiles.remove(file);
        if(correct == 0){
            faceOne = new Texture(Gdx.files.absolute(file.getPath()));
            faceTwo = new Texture(Gdx.files.absolute(validFiles.get((int)(Math.random()*validFiles.size())).getPath()));
        } else {

            faceTwo = new Texture(Gdx.files.absolute(file.getPath()));
            faceOne = new Texture(Gdx.files.absolute(validFiles.get((int)(Math.random()*validFiles.size())).getPath()));
        }

        spriteOne = new Sprite(faceOne);
        spriteTwo = new Sprite(faceTwo);


        answerOne.x = (screenWidth - spriteOne.getWidth()/6)/18;
        answerOne.y = (screenHeight - spriteOne.getHeight()/6)/2;
        answerOne.width = spriteOne.getWidth()/6;
        answerOne.height = spriteOne.getHeight()/6;

        answerTwo.x = (screenWidth - spriteTwo.getWidth()/6) * 17 / 18;
        answerTwo.y = (screenHeight - spriteTwo.getHeight()/6)/2;
        answerTwo.width = spriteTwo.getWidth()/6;
        answerTwo.height = spriteTwo.getHeight()/6;

    }

}
