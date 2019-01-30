package com.lucidity.game;

import com.badlogic.gdx.Game;

import java.util.ArrayList;

/**
 * Created by lixiaoyan on 10/8/18.
 */

public class RecallGame extends Game {
    private String username;
    private ArrayList<String> locations;
    private ArrayList<String> personalEvents;
    private boolean isLucid, isPatient, isCare;
    private String dateTime;
    private String coordinates;
    public ActionResolver actionResolver;
    public ScorePoster scorePoster;

    public RecallGame(ActionResolver a, ScorePoster s,ArrayList<String> events, ArrayList<String> locs, String date, String location){
        username = a.getUsername();
        personalEvents = events;
        locations = locs;
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

    public ArrayList<String> getEvents() { return personalEvents; }
    public ArrayList<String> getLivedlocations() { return locations; }

    public boolean getLucid(){return isLucid;}
    public boolean getPatient(){return isPatient;}
    public boolean getCare(){return isCare;}
    public String getDateTime(){return dateTime;}
    public String getLocation(){return coordinates;}
}
