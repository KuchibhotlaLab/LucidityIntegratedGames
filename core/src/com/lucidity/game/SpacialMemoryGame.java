package com.lucidity.game;

import com.badlogic.gdx.Game;

import java.util.ArrayList;

/**
 * Created by lixiaoyan on 7/20/18.
 */

public class SpacialMemoryGame extends Game {
    private String username;
    private boolean isLucid, isPatient, isCare;
    private String dateTime;
    private String coordinates;
    public ActionResolver actionResolver;


    //TODO: reduce the amount of things passed by intent(they can all be extracted from action resolver)
    public SpacialMemoryGame(ActionResolver a, String uname, String date, String location,
                             boolean lucid, boolean patient, boolean care){
        username = uname;

        isLucid = lucid;
        isPatient = patient;
        isCare = care;
        dateTime = date;
        coordinates = location;
        actionResolver = a;
    }

    @Override
    public void create() {
        showLoadingScreen();
    }

    public void showLoadingScreen() {
        setScreen(new LoadingScreen(this));
    }

    public String getUsername() {
        return username;
    }

    public boolean getLucid(){return isLucid;}
    public boolean getPatient(){return isPatient;}
    public boolean getCare(){return isCare;}
    public String getDateTime(){return dateTime;}
    public String getLocation(){return coordinates;}
}

