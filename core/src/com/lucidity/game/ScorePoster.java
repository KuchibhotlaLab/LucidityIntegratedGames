package com.lucidity.game;

public interface ScorePoster {
    void postScoreBlock(String username, String dateTime, String location, String menu,
                        String difficulty, int score, int[] trialSuccess, double[][] attemptTime);

    void postScoreObj(String username, String dateTime, String location, String menu,
                      String difficulty, int score, int[] trialSuccess, double[] trialTime);

    void postScoreSp(String username, String dateTime, String location, String menu,
                      String difficulty, int score, int[] trialSuccess, double[] trialTime);

    void postScoreNtF(String username, String dateTime, String location, String menu,
                      int score, int[] trialSuccess, double[] trialTime);

    void postScoreFtN(String username, String dateTime, String location, String menu,
                      int score, int[] trialSuccess, double[] trialTime);

    void postOnline(String username);
}
