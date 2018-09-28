package com.lucidity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by lixiaoyan on 7/6/18.
 */

public class LoadingScreen extends InputAdapter implements Screen {
    private WorkingMemoryGame memGame;
    private FacialMemoryGame FacMemGame;
    private ObjectRecognitionGame ObjRecGame;
    private SpatialMemoryGame SpMemGame;
    boolean isMemGame, isFacGame, isObjGame, isSpGame = false;

    ShapeRenderer renderer;
    SpriteBatch batch;
    FitViewport viewport;


    Texture background;
    TextureRegion textureRegion;
    Sprite resizedBg;

    BitmapFont font;
    float elapsed;

    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();

    public LoadingScreen(WorkingMemoryGame game) {
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        isMemGame = true;
        this.memGame = game;
    }

    public LoadingScreen(FacialMemoryGame game) {
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        isFacGame = true;
        this.FacMemGame = game;
    }
    public LoadingScreen(ObjectRecognitionGame game) {
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        isObjGame = true;
        this.ObjRecGame = game;
    }

    public LoadingScreen(SpatialMemoryGame game) {
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        isSpGame = true;
        this.SpMemGame = game;
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(GameTwoConstants.MODE_WORLD_SIZE, GameTwoConstants.MODE_WORLD_SIZE);

        textureRegion= new TextureRegion(background, 0, 0, background.getWidth(), background.getHeight());
        resizedBg = new Sprite(textureRegion);
        resizedBg.setSize(1f,  resizedBg.getHeight() / resizedBg.getWidth());

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(GameTwoConstants.MODE_LABEL_SCALE);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        //viewport.apply();
        if(isMemGame) {
            Gdx.gl.glClearColor(GameOneConstants.LOADING_COLOR.r, GameOneConstants.LOADING_COLOR.g, GameOneConstants.LOADING_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else if(isFacGame) {
            Gdx.gl.glClearColor(GameTwoConstants.BACKGROUND_COLOR.r, GameTwoConstants.BACKGROUND_COLOR.g, GameTwoConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else if(isObjGame){
            Gdx.gl.glClearColor(GameThreeConstants.LOADING_COLOR.r, GameThreeConstants.LOADING_COLOR.g, GameThreeConstants.LOADING_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else if(isSpGame){
            //Gdx.gl.glClearColor(GameFourConstants.LOADING_COLOR.r, GameFourConstants.LOADING_COLOR.g, GameFourConstants.LOADING_COLOR.b, 1);
            //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            batch.draw(resizedBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }


        if (elapsed < 2) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


            batch.begin();
            font.getData().setScale(GameTwoConstants.TITLE_SCALE);

            if(isMemGame) {
                font.setColor(GameOneConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameOneConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) /2,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameOneConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) ,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height);
            } else if(isFacGame){
                font.setColor(GameTwoConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameTwoConstants.TITLE_THREE);
                font.draw(batch, promptLayout_three, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width) / 2,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameTwoConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2 / 3,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameTwoConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) * 9 / 10,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);
            } else if(isObjGame){
                font.setColor(GameThreeConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameThreeConstants.TITLE_THREE);
                font.draw(batch, promptLayout_three, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width)  / 2,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameThreeConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2 / 3,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, GameThreeConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, (GameOneConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) * 5 / 4,
                        GameOneConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);
            } else if (isSpGame){
                font.setColor(Color.WHITE);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, GameFourConstants.TITLE_ONE);
                font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, GameFourConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2,
                        screenHeight * 0.75f - 2f * promptLayout_three.height);
            }

            batch.end();
        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            batch.begin();
            font.getData().setScale(GameOneConstants.NOTIFICATION_SCALE);

            if (isMemGame) {
                font.setColor(GameOneConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, GameOneConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, GameOneConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, GameOneConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);
            } else if (isFacGame) {
                font.setColor(GameTwoConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, GameTwoConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, GameTwoConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, GameTwoConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);
            } else if (isObjGame) {
                font.setColor(GameThreeConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, GameThreeConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, GameThreeConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, GameThreeConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);
            } else if (isSpGame) {
                font.setColor(GameFourConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, GameFourConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, GameFourConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, GameFourConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);

                final GlyphLayout instructLayout_four = new GlyphLayout(font, GameFourConstants.INSTRUCT_FOUR);
                font.draw(batch, instructLayout_four, (screenWidth - instructLayout_four.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height - 2.0f * instructLayout_three.height);

                final GlyphLayout instructLayout_five = new GlyphLayout(font, GameFourConstants.INSTRUCT_FIVE);
                font.draw(batch, instructLayout_five, (screenWidth - instructLayout_five.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height
                                - 2.0f * instructLayout_three.height - 2.0f * instructLayout_four.height);

                final GlyphLayout instructLayout_six = new GlyphLayout(font, GameFourConstants.INSTRUCT_SIX);
                font.draw(batch, instructLayout_six, (screenWidth - instructLayout_six.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height
                                - 2.0f * instructLayout_three.height - 2.0f * instructLayout_four.height
                                - 2.0f * instructLayout_five.height);

                final GlyphLayout instructLayout_seven = new GlyphLayout(font, GameFourConstants.INSTRUCT_SEVEN);
                font.draw(batch, instructLayout_seven, (screenWidth - instructLayout_seven.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height
                                - 2.0f * instructLayout_three.height - 2.0f * instructLayout_four.height
                                - 2.0f * instructLayout_five.height - 2.0f * instructLayout_six.height);
            }
            final GlyphLayout skipLayout = new GlyphLayout(font, GameOneConstants.LOADING_SCREEN_SKIP);
            font.draw(batch, skipLayout, (screenWidth - skipLayout.width) / 2,
                    screenHeight * 0.2f);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true);}

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
        if (isMemGame) {
            int diff = memGame.actionResolver.getDifficulty();
            if (diff == -1) {
                memGame.setScreen(new DifficultyScreen(memGame));
            } else {
                memGame.setScreen(new MemoryScreen(memGame, diff,0,1));
            }
        } else if (isFacGame) {
            if (FacMemGame.actionResolver.getLucidity() || FacMemGame.actionResolver.getCare()) {
                int modeSelect = (int) (Math.random() * 4);
                switch (modeSelect) {
                    case 0:
                        FacMemGame.setScreen(new FaceToNameScreen(FacMemGame, 0, 1, GameTwoConstants.MODE_NAME));
                        break;
                    case 1:
                        FacMemGame.setScreen(new FaceToNameScreen(FacMemGame, 0, 1, GameTwoConstants.MODE_ATTR));
                        break;
                    case 2:
                        FacMemGame.setScreen(new NameToFaceScreen(FacMemGame, 0, 1, GameTwoConstants.MODE_NAME));
                        break;
                    case 3:
                        FacMemGame.setScreen(new NameToFaceScreen(FacMemGame, 0, 1, GameTwoConstants.MODE_ATTR));
                        break;
                }
            } else {
                FacMemGame.setScreen(new ModeScreen(FacMemGame));
            }
        } else if (isObjGame) {
            int diff = ObjRecGame.actionResolver.getDifficulty();
            if (diff == -1) {
                ObjRecGame.setScreen(new DifficultyScreen(ObjRecGame));
            } else {
                ObjRecGame.setScreen(new ObjectRecognitionScreen(ObjRecGame, diff));
            }
        } else if (isSpGame) {
            int diff = SpMemGame.actionResolver.getDifficulty();
            if (diff == -1) {
                SpMemGame.setScreen(new DifficultyScreen(SpMemGame));
            } else {
                SpMemGame.setScreen(new SpatialScreen(SpMemGame, diff));
            }
        }
        return false;
    }
}
