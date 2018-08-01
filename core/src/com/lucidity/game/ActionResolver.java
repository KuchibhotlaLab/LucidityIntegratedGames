package com.lucidity.game;

/**
 * Created by lixiaoyan on 7/20/18.
 */


public interface ActionResolver {
    public void MemoryGame();
    public void FaceGame();
    public void ObjectGame();
    public void SpaceGame();
    public boolean getLucidity();
    public boolean getCare();
    public boolean getPatient();
    public String getUsername();
    public String getOrder();
    public int getCounter();
    public void NextGame();
}
