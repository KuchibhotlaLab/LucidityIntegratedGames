package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ObjGameScores")
public class ObjGameScore {
    @NonNull
    private String username;
    @PrimaryKey
    @NonNull
    private String time;
    private String location;
    private String menu;
    private String difficulty;
    private String score;
    private int trial1;
    private double trialtime1;
    private int trial2;
    private double trialtime2;
    private int trial3;
    private double trialtime3;
    private int trial4;
    private double trialtime4;
    private int trial5;
    private double trialtime5;

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

    public int getTrial1() {return trial1;}


}
