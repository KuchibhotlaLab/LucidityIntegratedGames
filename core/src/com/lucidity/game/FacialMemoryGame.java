package com.lucidity.game;

import com.badlogic.gdx.Game;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class FacialMemoryGame extends Game {
    private String username;

    public FacialMemoryGame(String uname){
        username = uname;
    }

    @Override
    public void create() {
        showLoadingScreen();
    }

    public void showLoadingScreen() {
        setScreen(new FacialLoadingScreen(this));
    }

    public String getUsername() {
        return username;
    }

}
