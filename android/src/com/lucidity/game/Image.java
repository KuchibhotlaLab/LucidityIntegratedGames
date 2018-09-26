package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by gurleensethi on 02/02/18.
 */

@Entity(tableName = "Images")
public class Image {
    @PrimaryKey
    @NonNull
    private String username;
    private String fileName;
    private String imageName;
    private String imageRelation;
    private char gender;

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String uname) {
        this.username = uname;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fname) {
        this.fileName = fname;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String iname) {
        this.imageName = iname;
    }

    public String getImageRelation() {
        return imageRelation;
    }

    public void setImageRelation(String relation) {
        this.imageRelation = relation;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char g) {
        gender = g;
    }
}