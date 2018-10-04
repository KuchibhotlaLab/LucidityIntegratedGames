package com.lucidity.game;

public interface ScorePoster {
    void postScoreObj(String username, String dateTime, String location, String menu,
                             String difficulty, int score, int[] trialSuccess, double[] trialTime);
}
