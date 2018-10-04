package com.lucidity.game;

import android.content.Context;

public class ScorePosterAndroid implements ScorePoster {
    Context context;

    public ScorePosterAndroid(Context c) {
        context = c;
    }

    public void postScoreObj(String username, String dateTime, String location, String menu,
                             String difficulty, int score, int[] trialSuccess, double[] trialTime) {

    }
}
