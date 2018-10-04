package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BlockGameScoreDAO {
    @Insert
    void insert(BlockGameScore score);

    @Delete
    void delete(BlockGameScore score);

    @Query("SELECT * FROM BlockGameScores WHERE username = :uname")
    List<BlockGameScore> getUserBlockGameScores(String uname);
}
