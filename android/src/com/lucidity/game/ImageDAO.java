package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ImageDAO {
    @Insert
    void insert(Image image);

    @Delete
    void delete(Image image);

    @Query("SELECT * FROM Images WHERE username = :uname")
    List<Image> getUserImages(String uname);

    @Query("SELECT * FROM Images WHERE username = :uname AND fileName = :fname")
    Image getImage(String uname, String fname);
}
