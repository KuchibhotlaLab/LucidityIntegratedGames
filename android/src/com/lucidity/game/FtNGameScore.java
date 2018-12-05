package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "FtNGameScores")
public class FtNGameScore {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @NonNull
    private String username;
    @NonNull
    private String time;
    private String location;
    private String menu;
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
    public String getTime() {return time;}
    public void setTime(@NonNull String t) {this.time = t;}

    public String getLocation() {return location;}
    public void setLocation(String l) {this.location = l;}

    public String getMenu() {return menu;}
    public void setMenu(String m) {this.menu = m;}

    public String getScore() {return score;}
    public void setScore(String s) {score = s;}

    public int getTrial1() {return trial1;}
    public void setTrial1(int t1) {trial1 = t1;}
    public double getTrialtime1() {return trialtime1;}
    public void setTrialtime1(double t1time) {trialtime1 = t1time;}

    public int getTrial2() {return trial2;}
    public void setTrial2(int t2) {trial2 = t2;}
    public double getTrialtime2() {return trialtime2;}
    public void setTrialtime2(double t2time) {trialtime2 = t2time;}

    public int getTrial3() {return trial3;}
    public void setTrial3(int t3) {trial3 = t3;}
    public double getTrialtime3() {return trialtime3;}
    public void setTrialtime3(double t3time) {trialtime3 = t3time;}

    public int getTrial4() {return trial4;}
    public void setTrial4(int t4) {trial4 = t4;}
    public double getTrialtime4() {return trialtime4;}
    public void setTrialtime4(double t4time) {trialtime4 = t4time;}

    public int getTrial5() {return trial5;}
    public void setTrial5(int t5) {trial5 = t5;}
    public double getTrialtime5() {return trialtime5;}
    public void setTrialtime5(double t5time) {trialtime5 = t5time;}
}
