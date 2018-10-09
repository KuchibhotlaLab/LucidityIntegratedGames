package com.lucidity.game;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

public class ScorePosterAndroid implements ScorePoster {
    Context context;

    public ScorePosterAndroid(Context c) {
        context = c;
    }

    public void postScoreBlock(String username, String dateTime, String location, String menu,
                               String difficulty, int score, int[] trialSuccess, double[][] attemptTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-BlockGameScores")
                .build();
        BlockGameScoreDAO gameScoreDAO = database.getBlockGameScoreDAO();

        BlockGameScore gameScore = new BlockGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setDifficulty(difficulty);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime11(attemptTime[0][0]);
        gameScore.setTrialtime12(attemptTime[0][1]);
        gameScore.setTrialtime13(attemptTime[0][2]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime21(attemptTime[1][0]);
        gameScore.setTrialtime22(attemptTime[1][1]);
        gameScore.setTrialtime23(attemptTime[1][2]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime31(attemptTime[2][0]);
        gameScore.setTrialtime32(attemptTime[2][1]);
        gameScore.setTrialtime33(attemptTime[2][2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime41(attemptTime[3][0]);
        gameScore.setTrialtime42(attemptTime[3][1]);
        gameScore.setTrialtime43(attemptTime[3][2]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime51(attemptTime[4][0]);
        gameScore.setTrialtime52(attemptTime[4][1]);
        gameScore.setTrialtime53(attemptTime[4][2]);

        gameScoreDAO.insert(gameScore);
    }

    public void postScoreObj(String username, String dateTime, String location, String menu,
                             String difficulty, int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ObjGameScores")
                .build();
        ObjGameScoreDAO gameScoreDAO = database.getObjGameScoreDAO();

        ObjGameScore gameScore = new ObjGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setDifficulty(difficulty);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
    }

    public void postScoreSp(String username, String dateTime, String location, String menu,
                     String difficulty, int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-SpGameScores")
                .build();
        SpGameScoreDAO gameScoreDAO = database.getSpGameScoreDAO();

        SpGameScore gameScore = new SpGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setDifficulty(difficulty);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
    }

    public void postScoreNtF(String username, String dateTime, String location, String menu,
                      int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-NtFGameScores")
                .build();
        NtFGameScoreDAO gameScoreDAO = database.getNtFGameScoreDAO();

        NtFGameScore gameScore = new NtFGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
    }

    public void postScoreFtN(String username, String dateTime, String location, String menu,
                      int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FtNGameScores")
                .build();
        FtNGameScoreDAO gameScoreDAO = database.getFtNGameScoreDAO();

        FtNGameScore gameScore = new FtNGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);

    }
}
