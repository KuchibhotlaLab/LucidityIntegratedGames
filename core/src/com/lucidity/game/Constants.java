package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
    public static final float WORLD_SIZE = 10.0f;
    public static final Color BACKGROUND_COLOR = new Color(0.93f,0.92f,0.9f, 1);

    public static final float PLAYER_HEAD_RADIUS = 0.5f;
    public static final float PLAYER_HEAD_HEIGHT = 4.0f * PLAYER_HEAD_RADIUS;
    public static final float PLAYER_LIMB_WIDTH = 0.1f;
    public static final int PLAYER_HEAD_SEGMENTS = 20;
    public static final Color PLAYER_COLOR = Color.BLACK;
    public static final float PLAYER_MOVEMENT_SPEED = 10.0f;

    public static final float ACCELEROMETER_SENSITIVITY = 0.5f;
    public static final float GRAVITATIONAL_ACCELERATION = 9.8f;

    public static final float ICICLES_HEIGHT = 1.0f;
    public static final float ICICLES_WIDTH = 0.5f;
    public static final Vector2 ICICLES_ACCELERATION = new Vector2(0, -5.0f);
    public static final Color ICICLE_COLOR = Color.WHITE;
    public static final int INITIAL_ICICLES_ARRAY_CAPACITY = 100;

    public static final float HUD_FONT_REFERENCE_SCREEN_SIZE = 480.0f;
    public static final float HUD_MARGIN = 20.0f;

    public static final String EASY_LABEL = "Easy";
    public static final String MEDIUM_LABEL = "Medium";
    public static final String HARD_LABEL = "Hard";

    public static final float EASY_SPAWNS_PER_SECOND = 5;
    public static final float MEDIUM_SPAWNS_PER_SECOND = 15;
    public static final float HARD_SPAWNS_PER_SECOND = 25;

    public static final Color EASY_COLOR = new Color(1.0f,0.98f,0.78f, 1);
    public static final Color MEDIUM_COLOR = new Color(0.98f,0.92f,0.72f, 1);
    public static final Color HARD_COLOR = new Color(1.0f,0.88f,0.51f, 1);

    public static final float DIFFICULTY_WORLD_SIZE = 480.0f;
    public static final float DIFFICULTY_BUBBLE_RADIUS = DIFFICULTY_WORLD_SIZE / 9;
    public static final float DIFFICULTY_LABEL_SCALE = 1.5f;

    public static final Vector2 EASY_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE / 4, DIFFICULTY_WORLD_SIZE / 2);
    public static final Vector2 MEDIUM_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE / 2, DIFFICULTY_WORLD_SIZE / 2);
    public static final Vector2 HARD_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE * 3 / 4, DIFFICULTY_WORLD_SIZE / 2);
    public static final String DIFFICULTY_LABEL = "Difficulty: ";
    public static final String SCORE_LABEL = "Score: ";
    public static final String DEATHS_LABEL = "Deaths: ";
    public static final String TOP_SCORE_LABEL = "Top Score: ";
    public static final int SCORE_CENTER = 70;

    public static final String TRIAL_LABEL = "Trial: ";
    public static final int TRIAL_CENTER = 400;

    public static final int SECONDS_DElAYED = 1;

    /*public static final float SUBMIT_BUTTON_WIDTH = DIFFICULTY_WORLD_SIZE  / 3;
    public static final float SUBMIT_BUTTON_HEIGHT = DIFFICULTY_WORLD_SIZE / 6;
    public static final Vector2 SUBMIT_BUTTON_CENTER = new Vector2(DIFFICULTY_WORLD_SIZE / 2, DIFFICULTY_WORLD_SIZE * 5 / 6);
    public static final Color SUBMIT_BUTTON_COLOR =  new Color(1, 1, 1, 0);*/


    public enum Difficulty {
        EASY(EASY_SPAWNS_PER_SECOND, EASY_LABEL),
        MEDIUM(MEDIUM_SPAWNS_PER_SECOND, MEDIUM_LABEL),
        HARD(HARD_SPAWNS_PER_SECOND, HARD_LABEL);

        float spawnRate;
        String label;

        Difficulty(float spawnRate, String label) {
            this.spawnRate = spawnRate;
            this.label = label;
        }
    }
}
