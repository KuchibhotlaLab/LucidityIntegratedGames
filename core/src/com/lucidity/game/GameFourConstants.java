package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by lixiaoyan on 7/20/18.
 */

public class GameFourConstants {
    public static final float WORLD_SIZE = 10.0f;

    //https://loading.io/color/feature/Blue
    public static final Color LOADING_COLOR = new Color(0.89f, 0.95f, 1, 1);
    public static final Color BACKGROUND_COLOR = new Color(0.54f, 0.75f, 0.97f, 1);


    public static final Color TITLE_COLOR = new Color(0.11f,0.46f,0.75f, 1);

    public static final Color EASY_COLOR = new Color(0.25f,0.56f,0.88f, 1);
    public static final Color MEDIUM_COLOR = TITLE_COLOR;
    public static final Color HARD_COLOR = new Color(0f,0.32f,0.64f, 1);

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    public static final String TITLE_ONE = "Spacial";
    public static final String TITLE_TWO = "Memory Test";

    public static final String INSTRUCTIONS = "Remember the roadblocks";
    public static final String INSTRUCTIONS_TWO = "Find a path between";
    public static final String INSTRUCTIONS_THREE = "the lit up blocks";
    public static final String INSTRUCTIONS_FOUR = "without the road blocks";
    public static final String SUBMIT_TEXT = "Submit";

    public static final float INSTRUCTION_SIZE = 3f;
}
