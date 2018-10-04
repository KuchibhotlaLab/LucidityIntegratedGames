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
    private ArrayList<String> picturegenders;
    private boolean isLucid, isPatient, isCare;
    private String dateTime;
    private String coordinates;
    public ActionResolver actionResolver;
    public ScorePoster scorePoster;


    //TODO: reduce repetition in code/think location getter
    public FacialMemoryGame(ActionResolver a, ScorePoster s, ArrayList<String> pnames, ArrayList<ArrayList<String>> ptags,
                            ArrayList<String> pgenders, String date, String location){
        username = a.getUsername();
        picturenames = pnames;
        picturetags = ptags;
        picturegenders = pgenders;
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
        showLoadingScreen();
    }

    public void showLoadingScreen() {
        setScreen(new LoadingScreen(this));
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getPicturenames() { return picturenames; }

    public ArrayList<ArrayList<String>> getPicturetags() { return picturetags; }

    public ArrayList<String> getPicturegenders() { return picturegenders; }

    public boolean getLucid(){return isLucid;}
    public boolean getPatient(){return isPatient;}
    public boolean getCare(){return isCare;}
    public String getDateTime(){return dateTime;}
    public String getLocation(){return coordinates;}
}
