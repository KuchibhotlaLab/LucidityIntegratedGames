package com.lucidity.game;


import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
/**
 * Created by lixiaoyan on 7/3/18.
 */

public class PersonDependentGameLauncher extends AndroidApplication{
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new FacialMemoryGame(), config);
    }
}