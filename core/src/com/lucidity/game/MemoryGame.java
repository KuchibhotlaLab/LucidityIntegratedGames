package com.lucidity.game;

import com.badlogic.gdx.Game;

public class MemoryGame extends Game {
	@Override
	public void create() {
		showDifficultyScreen();
	}

	public void showDifficultyScreen() {
		setScreen(new DifficultyScreen(this));
	}

	public void showMemoryScreen(Constants.Difficulty difficulty) {
		setScreen(new MemoryScreen(this, difficulty, 0, 1));
	}

}