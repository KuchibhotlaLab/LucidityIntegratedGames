package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class GameOneConstants {
    public static final float WORLD_SIZE = 10.0f;
    public static final Color BACKGROUND_COLOR = new Color(0.93f,0.92f,0.9f, 1);
    public static final Color TITLE_COLOR = new Color(0.01f, 0.4f, 0.44f, 1);

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    public static final String EASY_LABEL = "Easy";
    public static final String MEDIUM_LABEL = "Medium";
    public static final String HARD_LABEL = "Hard";

    public static final Color EASY_COLOR = new Color(1.0f,0.98f,0.78f, 1);
    public static final Color MEDIUM_COLOR = new Color(0.98f,0.92f,0.72f, 1);
    public static final Color HARD_COLOR = new Color(1.0f,0.88f,0.51f, 1);

    public static final float DIFFICULTY_WORLD_SIZE = 480.0f;
    public static final float DIFFICULTY_BUBBLE_RADIUS = DIFFICULTY_WORLD_SIZE / 9;
    public static final float DIFFICULTY_LABEL_SCALE = .3f;

    public static final Vector2 EASY_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE / 4, DIFFICULTY_WORLD_SIZE / 2);
    public static final Vector2 MEDIUM_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE / 2, DIFFICULTY_WORLD_SIZE / 2);
    public static final Vector2 HARD_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE * 3 / 4, DIFFICULTY_WORLD_SIZE / 2);
    public static final String DIFFICULTY_LABEL = "Difficulty: ";
    public static final String SCORE_LABEL = "Score: ";
    public static final String DEATHS_LABEL = "Deaths: ";
    public static final String TOP_SCORE_LABEL = "Top Score: ";
    public static final int SCORE_CENTER = 70;

    public static final float TITLE_SCALE = .8f;
    public static final float NOTIFICATION_SCALE = .6f;
    public static final float LABEL_SCALE = .4f;

    public static final String TRIAL_LABEL = "Trial: ";
    public static final int TRIAL_CENTER = 400;

    public static final int SECONDS_DElAYED = 1;

    public static final String TITLE_ONE = "SHORT TERM";
    public static final String TITLE_TWO = "MEMORY GAME";
    public static final String CORRECT_MESSAGE = "Correct!";
    public static final String INCORRECT_MESSAGE = "Incorrect!";
    public static final String PROMPT_ONE = "Remember the position";
    public static final String PROMPT_TWO = "of the lit up boxes.";
    public static final String WAIT_ONE = "Select the blocks";
    public static final String WAIT_TWO = "after this screen ends";

    public static final String END_INSTRUCTIONS_ONE = "Please tap anywhere";
    public static final String END_INSTRUCTIONS_TWO = "to continue";

    public static final Color CORRECT_COLOR = new Color(0.6f, 0.89f, 0.55f, 1);
    public static final Color INCORRECT_COLOR = new Color(1f, 0.41f, 0.38f, 1);
}
