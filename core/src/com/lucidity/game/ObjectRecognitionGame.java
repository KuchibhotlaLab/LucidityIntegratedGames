package com.lucidity.game;

import com.badlogic.gdx.Game;

/**
 * Created by lixiaoyan on 7/12/18.
 */

public class ObjectRecognitionGame extends Game {

    @Override
    public void create() {
        showDifficultyScreen();
    }

    public void showDifficultyScreen() {setScreen(new LoadingScreen(this));
    }

    public void showObjectRecognitionScreen(GameOneConstants.Difficulty difficulty) {
        //setScreen(new MemoryScreen(this, difficulty, 0, 1));
        setScreen(new ObjectRecognitionDifficultyScreen(this));
    }
}
