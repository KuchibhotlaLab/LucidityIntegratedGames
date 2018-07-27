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
    private boolean isLucid, isPatient, isCare;
    private String dateTime;
    private String coordinates;
    public ActionResolver actionResolver;


    //TODO: reduce repetition in code/think location getter
    public FacialMemoryGame(ActionResolver a, ArrayList<String> pnames, ArrayList<ArrayList<String>> ptags,
                            String date, String location){
        username = a.getUsername();
        picturenames = pnames;
        picturetags = ptags;
        isLucid = a.getLucidity();
        isPatient = a.getPatient();
        isCare = a.getCare();
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

    public ArrayList<String> getPicturenames() { return picturenames; }

    public ArrayList<ArrayList<String>> getPicturetags() { return picturetags; }

    public boolean getLucid(){return isLucid;}
    public boolean getPatient(){return isPatient;}
    public boolean getCare(){return isCare;}
    public String getDateTime(){return dateTime;}
    public String getLocation(){return coordinates;}
}
