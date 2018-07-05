package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class GameTwoConstants {
    //theme: digitalsynopsis.com/design/minimal-web-color-palettes-combination-hex-code/
    public static final float WORLD_SIZE = 10.0f;

    public static final float MODE_WORLD_SIZE = 480.0f;
    public static final float MODE_BUBBLE_RADIUS = MODE_WORLD_SIZE / 5;
    public static final float MODE_LABEL_SCALE = 1.5f;

    public static final Color BACKGROUND_COLOR = new Color(0.97f,0.69f,0.58f, 1);
    public static final Color F2W_COLOR = new Color(0.96f, 0.45f, 0.5f, 1);
    public static final Color W2F_COLOR = new Color(0.75f, 0.42f, 0.52f, 1);
    public static final Color CHOICE_COLOR = new Color(0.96f, 0.45f, 0.5f, 1);
    public static final Color BUTTON_COLOR = new Color(0.21f, 0.36f, 0.49f, 1);
    public static final Color OUTLINE_COLOR = new Color(0.42f, 0.36f, 0.48f, 1);


    public static final Vector2 F2W_CENTER = new Vector2(MODE_WORLD_SIZE/4,MODE_WORLD_SIZE/2);
    public static final Vector2 W2F_CENTER = new Vector2(MODE_WORLD_SIZE * 3 /4,MODE_WORLD_SIZE/2);

    public static final String PROMPT = "Who is this";
    public static final float PROMPT_SCALE = 3f;


    public static final String SCORE_LABEL = "Score: ";
    public static final int SCORE_CENTER = 70;
    public static final String TRIAL_LABEL = "Trial: ";
    public static final int TRIAL_CENTER = 400;

    public static final float ANSWER_SCALE = 2f;
    public static final String CORRECT_MESSAGE = "You are correct!";
    public static final String INCORRECT_MESSAGE = "You are incorrect!";

    public static final String PROMPT_ONE = "Please identify the";
    public static final String PROMPT_TWO = "person in the picture.";
}
