package com.lucidity.game;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Image.class, BlockGameScore.class, ObjGameScore.class, SpGameScore.class, NtFGameScore.class, FtNGameScore.class}, version = 1)
public abstract class LucidityDatabase extends RoomDatabase {
    public abstract ImageDAO getImageDAO();
    public abstract BlockGameScoreDAO getBlockGameScoreDAO();
    public abstract ObjGameScoreDAO getObjGameScoreDAO();
    public abstract SpGameScoreDAO getSpGameScoreDAO();
    public abstract NtFGameScoreDAO getNtFGameScoreDAO();
    public abstract FtNGameScoreDAO getFtNGameScoreDAO();

}