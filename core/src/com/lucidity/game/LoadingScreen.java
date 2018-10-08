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
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by lixiaoyan on 7/6/18.
 */

public class LoadingScreen extends InputAdapter implements Screen {
    private WorkingMemoryGame memGame;
    private FacialMemoryGame FacMemGame;
    private ObjectRecognitionGame ObjRecGame;
    private SpatialMemoryGame SpMemGame;
    private RecallGame RecGame;
    boolean isMemGame, isFacGame, isObjGame, isSpGame, isRecGame = false;

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

    public LoadingScreen(RecallGame game){
        background = new Texture(Gdx.files.internal("data/bg-space-intro.jpg"));
        isRecGame = true;
        this.RecGame = game;
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(FacialGameConstants.MODE_WORLD_SIZE, FacialGameConstants.MODE_WORLD_SIZE);

        textureRegion= new TextureRegion(background, 0, 0, background.getWidth(), background.getHeight());
        resizedBg = new Sprite(textureRegion);
        resizedBg.setSize(1f,  resizedBg.getHeight() / resizedBg.getWidth());

        font = new BitmapFont(Gdx.files.internal("data/Kayak-Sans-Regular-large.fnt"), false);
        font.getData().setScale(FacialGameConstants.MODE_LABEL_SCALE);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        //viewport.apply();
        if(isMemGame) {
            Gdx.gl.glClearColor(BlockGameConstants.LOADING_COLOR.r, BlockGameConstants.LOADING_COLOR.g, BlockGameConstants.LOADING_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else if(isFacGame) {
            Gdx.gl.glClearColor(FacialGameConstants.BACKGROUND_COLOR.r, FacialGameConstants.BACKGROUND_COLOR.g, FacialGameConstants.BACKGROUND_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else if(isObjGame){
            Gdx.gl.glClearColor(ObjectGameConstants.LOADING_COLOR.r, ObjectGameConstants.LOADING_COLOR.g, ObjectGameConstants.LOADING_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else if(isSpGame){
            //Gdx.gl.glClearColor(SpacialGameConstants.LOADING_COLOR.r, SpacialGameConstants.LOADING_COLOR.g, SpacialGameConstants.LOADING_COLOR.b, 1);
            //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            batch.draw(resizedBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        } else if(isRecGame){
            Gdx.gl.glClearColor(RecallGameConstants.LOADING_COLOR.r, RecallGameConstants.LOADING_COLOR.g, RecallGameConstants.LOADING_COLOR.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }


        if (elapsed < 3) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


            batch.begin();
            font.getData().setScale(FacialGameConstants.TITLE_SCALE);

            if(isMemGame) {
                font.setColor(BlockGameConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_two = new GlyphLayout(font, BlockGameConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) /2,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, BlockGameConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, -(BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) ,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height);
            } else if(isFacGame){
                font.setColor(FacialGameConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, FacialGameConstants.TITLE_THREE);
                font.draw(batch, promptLayout_three, -(BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width) / 2,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, FacialGameConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2 / 3,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, FacialGameConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, (BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) * 9 / 10,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);
            } else if(isObjGame){
                font.setColor(ObjectGameConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, ObjectGameConstants.TITLE_THREE);
                font.draw(batch, promptLayout_three, (BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_three.width)  / 2,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, ObjectGameConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, -(BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_two.width) * 2 / 3,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_three.height);


                final GlyphLayout promptLayout_one = new GlyphLayout(font, ObjectGameConstants.TITLE_ONE);
                font.draw(batch, promptLayout_one, (BlockGameConstants.DIFFICULTY_WORLD_SIZE - promptLayout_one.width) * 5 / 4,
                        BlockGameConstants.DIFFICULTY_WORLD_SIZE * 2 + 1.5f * promptLayout_two.height + 1.5f * promptLayout_three.height);
            } else if (isSpGame){
                font.setColor(Color.WHITE);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, SpacialGameConstants.TITLE_ONE);
                font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, SpacialGameConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2,
                        screenHeight * 0.75f - 2f * promptLayout_three.height);
            } else if(isRecGame){
                font.setColor(RecallGameConstants.TITLE_COLOR);
                final GlyphLayout promptLayout_three = new GlyphLayout(font, RecallGameConstants.TITLE_ONE);
                font.draw(batch, promptLayout_three, (screenWidth - promptLayout_three.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout promptLayout_two = new GlyphLayout(font, RecallGameConstants.TITLE_TWO);
                font.draw(batch, promptLayout_two, (screenWidth - promptLayout_two.width) / 2,
                        screenHeight * 0.75f - 2f * promptLayout_three.height);

            }

            batch.end();
        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            batch.begin();
            font.getData().setScale(BlockGameConstants.NOTIFICATION_SCALE);

            if (isMemGame) {
                font.setColor(BlockGameConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, BlockGameConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, BlockGameConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, BlockGameConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);
            } else if (isFacGame) {
                font.setColor(FacialGameConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, FacialGameConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, FacialGameConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, FacialGameConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);
            } else if (isObjGame) {
                font.setColor(ObjectGameConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, ObjectGameConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, ObjectGameConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, ObjectGameConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);
            } else if (isSpGame) {
                font.setColor(SpacialGameConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);

                final GlyphLayout instructLayout_four = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_FOUR);
                font.draw(batch, instructLayout_four, (screenWidth - instructLayout_four.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height - 2.0f * instructLayout_three.height);

                final GlyphLayout instructLayout_five = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_FIVE);
                font.draw(batch, instructLayout_five, (screenWidth - instructLayout_five.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height
                                - 2.0f * instructLayout_three.height - 2.0f * instructLayout_four.height);

                final GlyphLayout instructLayout_six = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_SIX);
                font.draw(batch, instructLayout_six, (screenWidth - instructLayout_six.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height
                                - 2.0f * instructLayout_three.height - 2.0f * instructLayout_four.height
                                - 2.0f * instructLayout_five.height);

                final GlyphLayout instructLayout_seven = new GlyphLayout(font, SpacialGameConstants.INSTRUCT_SEVEN);
                font.draw(batch, instructLayout_seven, (screenWidth - instructLayout_seven.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height
                                - 2.0f * instructLayout_three.height - 2.0f * instructLayout_four.height
                                - 2.0f * instructLayout_five.height - 2.0f * instructLayout_six.height);
            } else if(isRecGame){
                font.setColor(RecallGameConstants.TITLE_COLOR);
                final GlyphLayout instructLayout_one = new GlyphLayout(font, RecallGameConstants.INSTRUCT_ONE);
                font.draw(batch, instructLayout_one, (screenWidth - instructLayout_one.width) / 2,
                        screenHeight * 0.75f);

                final GlyphLayout instructLayout_two = new GlyphLayout(font, RecallGameConstants.INSTRUCT_TWO);
                font.draw(batch, instructLayout_two, (screenWidth - instructLayout_two.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height);

                final GlyphLayout instructLayout_three = new GlyphLayout(font, RecallGameConstants.INSTRUCT_THREE);
                font.draw(batch, instructLayout_three, (screenWidth - instructLayout_three.width) / 2,
                        screenHeight * 0.75f - 2.0f * instructLayout_one.height - 2.0f * instructLayout_two.height);

            }
            final GlyphLayout skipLayout = new GlyphLayout(font, BlockGameConstants.LOADING_SCREEN_SKIP);
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
                        FacMemGame.setScreen(new FaceToNameScreen(FacMemGame, 0, 1, FacialGameConstants.MODE_NAME));
                        break;
                    case 1:
                        FacMemGame.setScreen(new FaceToNameScreen(FacMemGame, 0, 1, FacialGameConstants.MODE_ATTR));
                        break;
                    case 2:
                        FacMemGame.setScreen(new NameToFaceScreen(FacMemGame, 0, 1, FacialGameConstants.MODE_NAME));
                        break;
                    case 3:
                        FacMemGame.setScreen(new NameToFaceScreen(FacMemGame, 0, 1, FacialGameConstants.MODE_ATTR));
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
        } else if(isRecGame){
            RecGame.setScreen(new ModeScreen(RecGame));
        }
        return false;
    }
}
