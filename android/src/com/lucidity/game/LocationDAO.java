package com.lucidity.game;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LocationDAO {
    @Insert
    void insert(Location location);

    @Delete
    void delete(Location location);

    @Query("SELECT * FROM Locations WHERE username = :uname")
    List<Location> getUserLocations(String uname);

    @Query("SELECT * FROM Locations WHERE username = :uname AND location = :loc")
    Location getLocation(String uname, String loc);
}
