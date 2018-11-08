package com.lucidity.game;

import java.util.Random;

public class FullTestGenerator {
    static final int NUM_GAMES = 4;
    int[] gameOrder;

    public FullTestGenerator(){
        gameOrder = new int[NUM_GAMES];
        for( int i = 0; i < NUM_GAMES; i++){
            gameOrder[i] = i;
        }

        int index;
        int temp;
        Random random = new Random();
        for (int i = gameOrder.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = gameOrder[index];
            gameOrder[index] = gameOrder[i];
            gameOrder[i] = temp;
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
