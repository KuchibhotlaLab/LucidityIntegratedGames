package com.lucidity.game;

import com.badlogic.gdx.Game;

public class WorkingMemoryGame extends Game {
    private String username;

    public WorkingMemoryGame(String uname){
        username = uname;
    }

	@Override
	public void create() {
		showDifficultyScreen();
	}

	public void showDifficultyScreen() {
		setScreen(new DifficultyScreen(this));
	}

	public void showMemoryScreen(GameOneConstants.Difficulty difficulty) {
		double[] trialTimes = new double[5];
		setScreen(new MemoryScreen(this, difficulty, 0, 1, trialTimes));
	}

    public String getUsername() {
        return username;
    }
}