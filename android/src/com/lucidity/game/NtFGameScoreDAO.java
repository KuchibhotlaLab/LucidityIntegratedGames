package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NtFGameScoreDAO {
    @Insert
    void insert(NtFGameScore score);

    @Delete
    void delete(NtFGameScore score);

    @Query("SELECT * FROM NtFGameScores WHERE username = :uname")
    List<NtFGameScore> getUserNtFGameScores(String uname);
}
