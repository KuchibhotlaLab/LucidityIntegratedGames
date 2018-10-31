package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReGameScoreDAO {
    @Insert
    void insert(ReGameScore score);

    @Delete
    void delete(ReGameScore score);

    @Query("SELECT * FROM ReGameScores WHERE username = :uname")
    List<ReGameScore> getUserReGameScores(String uname);
}