package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FtNGameScoreDAO {
    @Insert
    void insert(FtNGameScore score);

    @Delete
    void delete(FtNGameScore score);

    @Query("SELECT * FROM FtNGameScores WHERE username = :uname")
    List<FtNGameScore> getUserFtNGameScores(String uname);
}
