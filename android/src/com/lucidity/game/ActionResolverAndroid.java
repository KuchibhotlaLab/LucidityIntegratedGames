package com.lucidity.game;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.lucidity.game.ActionResolver;

/**
 * Created by lixiaoyan on 7/20/18.
 */

public class ActionResolverAndroid implements ActionResolver {

    Handler handler;
    Context context;

    String username;
    boolean isLucid;
    boolean isCare;
    boolean isPatient;

    String gameType;


    public ActionResolverAndroid(Context context, String user, boolean lucid, boolean care, boolean patient) {
        handler = new Handler();
        this.context = context;
        username = user;
        isLucid = lucid;
        isCare = care;
        isPatient = patient;

    }

    public ActionResolverAndroid(Context context, String user, boolean lucid, boolean care, boolean patient, String game) {
        handler = new Handler();
        this.context = context;
        username = user;
        isLucid = lucid;
        isCare = care;
        isPatient = patient;
        gameType = game;
    }

    public void MemoryGame() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, AndroidLauncher.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", gameType);
                context.startActivity(i);
            }
        });


    }

    public void FaceGame() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, PersonDependentGameLauncher.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                context.startActivity(i);
            }
        });


    }

    public void ObjectGame() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, ObjectRecognitionGameLauncher.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                context.startActivity(i);
            }
        });
    }

    public void SpaceGame(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, ObjectRecognitionGameLauncher.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", gameType);
                context.startActivity(i);
            }
        });
    }
}
