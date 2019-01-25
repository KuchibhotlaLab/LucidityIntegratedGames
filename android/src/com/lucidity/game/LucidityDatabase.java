package com.lucidity.game;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Image.class, Location.class, History.class, BlockGameScore.class, ObjGameScore.class,
        SpGameScore.class, NtFGameScore.class, FtNGameScore.class, ReGameScore.class,
        FullTestRun.class}, version = 1, exportSchema = false)
public abstract class LucidityDatabase extends RoomDatabase {
    public abstract ImageDAO getImageDAO();
    public abstract LocationDAO getLocationDAO();
    public abstract BlockGameScoreDAO getBlockGameScoreDAO();
    public abstract ObjGameScoreDAO getObjGameScoreDAO();
    public abstract SpGameScoreDAO getSpGameScoreDAO();
    public abstract NtFGameScoreDAO getNtFGameScoreDAO();
    public abstract FtNGameScoreDAO getFtNGameScoreDAO();
    public abstract ReGameScoreDAO getReGameScoreDAO();
    public abstract FullTestRunDAO getFullTestRunDAO();
    public abstract HistoryDAO getHistoryDAO();
}