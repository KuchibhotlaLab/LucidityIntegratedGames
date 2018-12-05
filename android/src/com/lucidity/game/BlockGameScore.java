package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "BlockGameScores")
public class BlockGameScore {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @NonNull
    private String username;
    @NonNull
    private String time;
    private String location;
    private String menu;
    private String difficulty;
    private String score;
    private int trial1;
    private double trialtime11;
    private double trialtime12;
    private double trialtime13;
    private int trial2;
    private double trialtime21;
    private double trialtime22;
    private double trialtime23;
    private int trial3;
    private double trialtime31;
    private double trialtime32;
    private double trialtime33;
    private int trial4;
    private double trialtime41;
    private double trialtime42;
    private double trialtime43;
    private int trial5;
    private double trialtime51;
    private double trialtime52;
    private double trialtime53;

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

    public String getDifficulty() {return difficulty;}
    public void setDifficulty(String d) {this.difficulty = d;}

    public String getScore() {return score;}
    public void setScore(String s) {score = s;}

    public int getTrial1() {return trial1;}
    public void setTrial1(int t1) {trial1 = t1;}
    public double getTrialtime11() {return trialtime11;}
    public void setTrialtime11(double t11time) {trialtime11 = t11time;}
    public double getTrialtime12() {return trialtime12;}
    public void setTrialtime12(double t12time) {trialtime12 = t12time;}
    public double getTrialtime13() {return trialtime13;}
    public void setTrialtime13(double t13time) {trialtime13 = t13time;}

    public int getTrial2() {return trial2;}
    public void setTrial2(int t2) {trial2 = t2;}
    public double getTrialtime21() {return trialtime21;}
    public void setTrialtime21(double t21time) {trialtime21 = t21time;}
    public double getTrialtime22() {return trialtime22;}
    public void setTrialtime22(double t22time) {trialtime22 = t22time;}
    public double getTrialtime23() {return trialtime23;}
    public void setTrialtime23(double t23time) {trialtime23 = t23time;}

    public int getTrial3() {return trial3;}
    public void setTrial3(int t3) {trial3 = t3;}
    public double getTrialtime31() {return trialtime31;}
    public void setTrialtime31(double t31time) {trialtime31 = t31time;}
    public double getTrialtime32() {return trialtime32;}
    public void setTrialtime32(double t32time) {trialtime32 = t32time;}
    public double getTrialtime33() {return trialtime33;}
    public void setTrialtime33(double t33time) {trialtime33 = t33time;}

    public int getTrial4() {return trial4;}
    public void setTrial4(int t4) {trial4 = t4;}
    public double getTrialtime41() {return trialtime41;}
    public void setTrialtime41(double t41time) {trialtime41 = t41time;}
    public double getTrialtime42() {return trialtime42;}
    public void setTrialtime42(double t42time) {trialtime42 = t42time;}
    public double getTrialtime43() {return trialtime43;}
    public void setTrialtime43(double t43time) {trialtime43 = t43time;}

    public int getTrial5() {return trial5;}
    public void setTrial5(int t5) {trial5 = t5;}
    public double getTrialtime51() {return trialtime51;}
    public void setTrialtime51(double t51time) {trialtime51 = t51time;}
    public double getTrialtime52() {return trialtime52;}
    public void setTrialtime52(double t52time) {trialtime52 = t52time;}
    public double getTrialtime53() {return trialtime53;}
    public void setTrialtime53(double t53time) {trialtime53 = t53time;}
}
