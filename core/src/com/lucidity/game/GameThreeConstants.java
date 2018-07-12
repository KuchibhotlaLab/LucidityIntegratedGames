package com.lucidity.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by lixiaoyan on 7/12/18.
 */

public class GameThreeConstants {
    //theme: www.colorcombos.com/color-schemes/294/ColorCombo294.html
    public static final float WORLD_SIZE = 10.0f;

    public static final float MODE_WORLD_SIZE = 480.0f;
    public static final float MODE_BUBBLE_RADIUS = MODE_WORLD_SIZE / 8;
    public static final float MODE_LABEL_SCALE = 1f;

    public static final Color LOADING_COLOR = new Color(0.96f,0.94f,0.8f, 1);
    public static final Color BACKGROUND_COLOR = new Color(0.87f,0.82f,0.62f, 1);
    public static final Color TITLE_COLOR = new Color(0.7f,0.65f,0.5f, 1);

    public static final Color EASY_COLOR = new Color(0.72f,0.78f,0.55f, 1);
    public static final Color MEDIUM_COLOR = TITLE_COLOR;
    public static final Color HARD_COLOR = new Color(0.41f,0.34f,0.26f, 1);

    public static final Vector2 EASY_CENTER = new Vector2(MODE_WORLD_SIZE / 4, MODE_WORLD_SIZE / 2);
    public static final Vector2 MEDIUM_CENTER = new Vector2(MODE_WORLD_SIZE / 2, MODE_WORLD_SIZE / 2);
    public static final Vector2 HARD_CENTER = new Vector2(MODE_WORLD_SIZE * 3 / 4, MODE_WORLD_SIZE / 2);

    public static final String TITLE_ONE = "OBJECT";
    public static final String TITLE_TWO = "RECOGNITION";
    public static final String TITLE_THREE = "TEST";
}
