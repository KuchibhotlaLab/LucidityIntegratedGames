package com.lucidity.game;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

public class ScorePosterAndroid implements ScorePoster {
    Context context;

    public ScorePosterAndroid(Context c) {
        context = c;
    }

    public void postScoreObj(String username, String dateTime, String location, String menu,
                             String difficulty, int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ObjGameScores")
                .build();
        ObjGameScoreDAO gameScoreDAO = database.getObjGameScoreDAO();

        ObjGameScore gameScore= new ObjGameScore();
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
}
