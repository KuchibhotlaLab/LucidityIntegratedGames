package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by lixiaoyan on 7/20/18.
 */

public class SpatialGameConstants {
    public static final float WORLD_SIZE = 10.0f;

    //https://loading.io/color/feature/Blue
    //https://openglcolor.mpeters.me/
    public static final Color LOADING_COLOR = new Color(0.89f, 0.95f, 1, 1);
    public static final Color BACKGROUND_COLOR = new Color(0.54f, 0.75f, 0.97f, 1);
    public static final Color BACKGROUND_COLOR_TOP = new Color(0.196f, 0.298f, 0.404f, 1);
    public static final Color BACKGROUND_COLOR_BOT = new Color(0.200f, 0.451f, 0.530f, 1);


    public static final Color TITLE_COLOR = new Color(0.11f,0.46f,0.75f, 1);

    public static final Color EASY_COLOR = new Color(0.25f,0.56f,0.88f, 1);
    public static final Color MEDIUM_COLOR = TITLE_COLOR;
    public static final Color HARD_COLOR = new Color(0f,0.32f,0.64f, 1);
    public static final Color ROADBLOCK_COLOR = Color.valueOf("#413d5c");
    public static final Color START_END_COLOR = Color.valueOf("#e0a47c");
    public static final Color SQUARE_COLOR = Color.valueOf("a7dee7");
    public static final Color SELECTED_COLOR = Color.valueOf("3e7e94");
    public static final Color SUBMIT_PRESSED_COLOR = Color.valueOf("a36f4b");
    //public static final Color RESULT_COLOR = Color.valueOf("434b6e");

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    public static final String TITLE_ONE = "Spatial";
    public static final String TITLE_TWO = "Memory Test";

    public static final String INSTRUCTIONS = "Remember the roadblocks.";
    public static final String INSTRUCTIONS_TWO = "Find a viable path.";
    public static final String SUBMIT_TEXT = "Submit";

    public static final String INSTRUCT_ONE = "Remember the red";
    public static final String INSTRUCT_TWO = " roadblocks, then draw";
    public static final String INSTRUCT_THREE = "a connected path between";
    public static final String INSTRUCT_FOUR = "the green blocks without";
    public static final String INSTRUCT_FIVE = "going over the roadblocks.";
    public static final String INSTRUCT_SIX = "Two blocks must share";
    public static final String INSTRUCT_SEVEN = "an edge to be connected.";

    public static final float INSTRUCTION_SIZE = .6f;
    public static final float RESULT_SIZE = 0.65f;
}
