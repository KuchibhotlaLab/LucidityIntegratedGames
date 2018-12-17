package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Insert
    void insert(History history);

    @Delete
    void delete(History history);

    @Query("SELECT * FROM Histories WHERE username = :uname ORDER BY year ASC")
    List<History> getUserHistories(String uname);

    @Query("SELECT * FROM Histories WHERE username = :uname AND location = :loc AND event = :eve AND year = :years")
    History getEvent(String uname, String loc, String eve, String years);
}