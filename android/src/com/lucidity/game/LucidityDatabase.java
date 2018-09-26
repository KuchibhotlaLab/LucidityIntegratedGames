package com.lucidity.game;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Image.class}, version = 1)
public abstract class LucidityDatabase extends RoomDatabase {
    public abstract ImageDAO getImageDAO();
}