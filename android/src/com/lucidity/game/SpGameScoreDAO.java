package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SpGameScoreDAO {
    @Insert
    void insert(SpGameScore score);

    @Delete
    void delete(SpGameScore score);

    @Query("SELECT * FROM SpGameScores WHERE username = :uname")
    List<SpGameScore> getUserSpGameScores(String uname);
}
