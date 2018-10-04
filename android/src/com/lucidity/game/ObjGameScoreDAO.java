package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ObjGameScoreDAO {
    @Insert
    void insert(ObjGameScore score);

    @Delete
    void delete(ObjGameScore score);

    @Query("SELECT * FROM ObjGameScores WHERE username = :uname")
    List<ObjGameScore> getUserObjGameScores(String uname);
}
