package com.lucidity.game;

import com.badlogic.gdx.Game;

import java.util.ArrayList;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class FacialMemoryGame extends Game {
    private String username;
    private ArrayList<String> picturenames;
    private ArrayList<ArrayList<String>> picturetags;

    public FacialMemoryGame(String uname, ArrayList<String> pnames, ArrayList<ArrayList<String>> ptags){
        username = uname;
        picturenames = pnames;
        picturetags = ptags;
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

    public ArrayList<String> getPicturenames() { return picturenames; }

    public ArrayList<ArrayList<String>> getPicturetags() { return picturetags; }
}
