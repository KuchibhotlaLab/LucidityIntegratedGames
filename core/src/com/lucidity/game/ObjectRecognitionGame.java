package com.lucidity.game;

import com.badlogic.gdx.Game;

import java.util.ArrayList;

/**
 * Created by lixiaoyan on 7/12/18.
 */

public class ObjectRecognitionGame extends Game {
    private String username;
    private boolean isLucid, isPatient, isCare;
    private String dateTime;
    private String coordinates;
    public ActionResolver actionResolver;
    public ScorePoster scorePoster;

    public ObjectRecognitionGame(ActionResolver a, ScorePoster s, String date, String location){
        username = a.getUsername();
        isLucid = a.getLucidity();
        isPatient = a.getPatient();
        isCare = a.getCare();
        dateTime = date;
        coordinates = location;
        actionResolver = a;
        scorePoster = s;
    }


    @Override
    public void create() {
        showDifficultyScreen();
    }

    public void showDifficultyScreen() {setScreen(new LoadingScreen(this));}
    public String getUsername() {
        return username;
    }
    public boolean getLucid(){return isLucid;}
    public boolean getPatient(){return isPatient;}
    public boolean getCare(){return isCare;}
    public String getDateTime(){return dateTime;}
    public String getLocation(){return coordinates;}
}
