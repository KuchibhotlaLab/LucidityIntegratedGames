package com.lucidity.game;

/**
 * Created by lixiaoyan on 7/20/18.
 */


public interface ActionResolver {
    public boolean getLucidity();
    public boolean getCare();
    public boolean getPatient();
    public String getUsername();
    public int getCounter();
    public void NextGame();
    public int getDifficulty();
    public void setDifficulty(int d);
}
