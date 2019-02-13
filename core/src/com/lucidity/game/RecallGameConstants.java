package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by lixiaoyan on 10/8/18.
 */

public class RecallGameConstants {
    public static final float WORLD_SIZE = 10.0f;
    public static final float MODE_WORLD_SIZE = 480.0f;

    public static final float MODE_BUBBLE_RADIUS = MODE_WORLD_SIZE * 2 / 9;
    public static final float MODE_LABEL_SCALE = .3f;

    public static final Color LOADING_COLOR = new Color(0.97f, 0.69f, 0.58f, 1);
    public static final Color BACKGROUND_COLOR = new Color(0.98f, 0.76f, 0.7f, 1);
    public static final Color X_COLOR = new Color().valueOf("d66673");
    public static final Color TITLE_COLOR = new Color(0.42f,0.36f,0.47f, 1);

    public static final Color LOCATION_COLOR = new Color(0.76f,0.42f,0.52f, 1);
    public static final Color RELATION_COLOR = LOCATION_COLOR;

    public static final String MODE_ONE = "Identify";
    public static final String MODE_ONE_SECOND = "Location";
    public static final String MODE_ONE_THIRD = "Relations";


    public static final String TITLE_ONE = "Recall";
    public static final String TITLE_TWO = "Memory Test";

    public static final String SUBMIT_TEXT = "Submit";


    public static final Vector2 LOCATION_CENTER_NAME = new Vector2(MODE_WORLD_SIZE/4,MODE_WORLD_SIZE  * 3 / 4);
    public static final Vector2 LOCATION_CENTER_ATTR = new Vector2(MODE_WORLD_SIZE/4, MODE_WORLD_SIZE / 4);
    public static final Vector2 RELATION_CENTER_NAME = new Vector2(MODE_WORLD_SIZE * 3 /4,MODE_WORLD_SIZE * 3 /4);
    public static final Vector2 RELATION_CENTER_ATTR = new Vector2(MODE_WORLD_SIZE * 3 /4,MODE_WORLD_SIZE / 4);

    public static final String INSTRUCT_ONE = "Can you recall where";
    public static final String INSTRUCT_TWO = "each event in your";
    public static final String INSTRUCT_THREE = "personal life occured?";

    public static final String PROMPT_ONE = "Select the location where";
    public static final String PROMPT_LOCATION = "the following event occured.";
    public static final String PROMPT_TWO = "Where did this occur?";


    public static final String[] RANDOM_LOCATION_NAMES = {"Arlington", "Baton Rouge", "San Diego", "Kansas City", "Henderson", "Houston"};

    public static final float INSTRUCTION_SIZE = .6f;
    public static final float RESULT_SIZE = 0.65f;
}
