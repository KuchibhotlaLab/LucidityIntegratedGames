package com.lucidity.game;

import java.util.ArrayList;
import java.util.Random;

public class FullTestGenerator {
    static final int NUM_GAMES = 4;
    static final int NUM_GAME_TYPES = 5;
    int[] gameOrder;

    public FullTestGenerator(){
        ArrayList<Integer> games = new ArrayList<Integer>();
        for (int i = 0; i < NUM_GAME_TYPES; i++) {
            games.add(i);
        }

        int index;
        Random random = new Random();
        gameOrder = new int[NUM_GAMES];
        for( int i = 0; i < NUM_GAMES; i++){
            index = random.nextInt(games.size());
            gameOrder[i] = games.get(index);
            games.remove(index);
        }
    }

    @Override
    public String toString(){
        String order = "";
        for( int i = 0; i < NUM_GAMES; i++){
            order += gameOrder[i];
        }
        return order;
    }

    public String getGameType(int gameNum) {
        switch (gameNum){
            case 0:
                return "Block";
            case 1:
                return "Face";
            case 2:
                return "Object";
            case 3:
                return "Spatial";
            case 4:
                return "Recall";
        }

        return "";
    }
}
