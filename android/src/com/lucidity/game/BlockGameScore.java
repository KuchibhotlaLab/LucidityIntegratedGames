package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "BlockGameScores")
public class BlockGameScore {
    @NonNull
    private String username;
    @PrimaryKey
    @NonNull
    private String time;
    private String location;
    private String menu;
    private String difficulty;
    private String score;

    @NonNull
    public String getUsername() {
        return username;
    }
    public void setUsername(@NonNull String uname) {
        this.username = uname;
    }

    @NonNull
    public String getTime() {return time;}
    public void setTime(@NonNull String t) {this.time = t;}

    public String getLocation() {return location;}
    public void setLocation(String l) {this.location = l;}

    public String getMenu() {return menu;}
    public void setMenu(String m) {this.menu = m;}

    public String getDifficulty() {return difficulty;}
    public void setDifficulty(String d) {this.difficulty = d;}

    public String getScore() {return score;}
    public void setScore(String s) {score = s;}
}
