package com.lucidity.game;

import com.badlogic.gdx.Game;

public class WorkingMemoryGame extends Game {
    private String username;
    private boolean isLucid, isPatient, isCare;
    private String currentDateTime;
    private String coordinates;

    public WorkingMemoryGame(String uname, String date, String location, boolean lucid, boolean patient, boolean care){
        isLucid = lucid;
        isPatient = patient;
        isCare = care;

    	username = uname;
    	currentDateTime = date;
        coordinates = location;
    }

	@Override
	public void create() {
		showDifficultyScreen();
	}

	public void showDifficultyScreen() {
		setScreen(new DifficultyScreen(this));
	}

	public void showMemoryScreen(GameOneConstants.Difficulty difficulty) {
		setScreen(new MemoryScreen(this, difficulty, 0, 1));
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