package com.lucidity.game;


import android.content.Context;
import android.content.SharedPreferences;

public class Login {

    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    Context context;

    // Sharedpref file name
    private static final String PREF_NAME = "LucidityPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "username";

    //Make a new object to use shared preferences
    public Login(Context c){
        context = c;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Check if logged in
     * **/
    public boolean LoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Get username of logged in user
     * **/
    public String getUsername(){
        return pref.getString(KEY_NAME, null);
    }

    /**
     * Saves new login information
     * **/
    public void newLogin(String username){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, username);
        editor.commit();
    }

    /**
     * Deletes shared preferences
     * **/
    public void logout(){
        editor.clear();
        editor.commit();
    }
}
