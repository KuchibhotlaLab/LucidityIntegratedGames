package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Locations")
public class Location {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @NonNull
    private String username;
    @NonNull
    private String location;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String uname) {
        this.username = uname;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String loc) {
        this.location = loc;
    }
}
