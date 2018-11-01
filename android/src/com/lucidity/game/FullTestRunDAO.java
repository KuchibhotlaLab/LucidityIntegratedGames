package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FullTestRunDAO {
    @Insert
    void insert(FullTestRun tests);

    @Delete
    void delete(FullTestRun tests);

    @Query("SELECT * FROM FullTestRuns WHERE username = :uname")
    List<FullTestRun> getUserTestSuiteRuns(String uname);

    @Query("UPDATE FullTestRuns SET testtime1=:testTime WHERE username = :uname AND time = :startTime")
    void updateTime1(String testTime, String uname, String startTime);

    @Query("UPDATE FullTestRuns SET testtime2=:testTime WHERE username = :uname AND time = :startTime")
    void updateTime2(String testTime, String uname, String startTime);

    @Query("UPDATE FullTestRuns SET testtime3=:testTime WHERE username = :uname AND time = :startTime")
    void updateTime3(String testTime, String uname, String startTime);

    @Query("UPDATE FullTestRuns SET testtime4=:testTime WHERE username = :uname AND time = :startTime")
    void updateTime4(String testTime, String uname, String startTime);
}
