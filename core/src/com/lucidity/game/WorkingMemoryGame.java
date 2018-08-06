package com.lucidity.game;

import com.badlogic.gdx.Game;

public class WorkingMemoryGame extends Game {
    private String username;
    private boolean isLucid, isPatient, isCare;
    private String currentDateTime;
    private String coordinates;
    public ActionResolver actionResolver;


    public WorkingMemoryGame(ActionResolver a, String date, String location){
        isLucid = a.getLucidity();
        isPatient = a.getPatient();
        isCare = a.getCare();

    	username = a.getUsername();
    	currentDateTime = date;
        coordinates = location;
        actionResolver = a;
    }

	@Override
	public void create() { showLoadingScreen(); }

    public void showLoadingScreen() {
        setScreen(new LoadingScreen(this));
    }

    public String getUsername() {
        return username;
    }
    public String getDateTime() {return currentDateTime;}
    public boolean getLucid(){return isLucid;}
    public boolean getPatient(){return isPatient;}
    public boolean getCare(){return isCare;}
    public String getLocation(){return coordinates;}
}