package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "FullTestRuns")
public class FullTestRun {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @NonNull
    private String username;
    @NonNull
    private String time;
    private String menu;
    private String picture;
    private String testtype1;
    private String testtime1;
    private String testtype2;
    private String testtime2;
    private String testtype3;
    private String testtime3;
    private String testtype4;
    private String testtime4;
    private String testtype5;
    private String testtime5;

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

    public String getMenu() {return menu;}
    public void setMenu(String m) {this.menu = m;}

    public String getPicture() {return picture;}
    public void setPicture(String p) {this.picture = p;}

    public String getTesttype1() {return testtype1;}
    public void setTesttype1(String type1) {testtype1 = type1;}
    public String getTesttime1() {return testtime1;}
    public void setTesttime1(String time1) {testtime1 = time1;}

    public String getTesttype2() {return testtype2;}
    public void setTesttype2(String type2) {testtype2 = type2;}
    public String getTesttime2() {return testtime2;}
    public void setTesttime2(String time2) {testtime2 = time2;}

    public String getTesttype3() {return testtype3;}
    public void setTesttype3(String type3) {testtype3 = type3;}
    public String getTesttime3() {return testtime3;}
    public void setTesttime3(String time3) {testtime3 = time3;}

    public String getTesttype4() {return testtype4;}
    public void setTesttype4(String type4) {testtype4 = type4;}
    public String getTesttime4() {return testtime4;}
    public void setTesttime4(String time4) {testtime4 = time4;}

    public String getTesttype5() {return testtype5;}
    public void setTesttype5(String type5) {testtype5 = type5;}
    public String getTesttime5() {return testtime5;}
    public void setTesttime5(String time5) {testtime5 = time5;}
}