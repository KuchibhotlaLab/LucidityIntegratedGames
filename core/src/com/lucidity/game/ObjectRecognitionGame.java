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

    public ObjectRecognitionGame(String uname, String date, String location,
                                 boolean lucid, boolean patient, boolean care){
        username = uname;
        isLucid = lucid;
        isPatient = patient;
        isCare = care;
        dateTime = date;
        coordinates = location;
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
