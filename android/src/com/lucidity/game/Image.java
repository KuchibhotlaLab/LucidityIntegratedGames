package com.lucidity.game;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Images")
public class Image {
    @NonNull
    private String username;
    @PrimaryKey
    @NonNull
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

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fname) {
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