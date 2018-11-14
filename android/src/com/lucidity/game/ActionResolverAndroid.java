package com.lucidity.game;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

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
    String order;
    int counter;
    int difficulty;

    public ActionResolverAndroid(Context context, String user, boolean lucid, boolean care, boolean patient, String o, int c, int d) {
        handler = new Handler();
        this.context = context;
        username = user;
        isLucid = lucid;
        isCare = care;
        isPatient = patient;
        order = o;
        counter = c;
        difficulty = d;
    }

    public void NextGame(final String testSuiteStartTime) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "check order");
                i.putExtra("order", order);
                i.putExtra("counter", counter + 1);
                i.putExtra("difficulty", difficulty);
                i.putExtra("startTime", testSuiteStartTime);
                context.startActivity(i);
            }
        });
    }

    public boolean getLucidity(){return isLucid;}
    public boolean getCare(){return isCare;}
    public boolean getPatient(){return isPatient;}
    public String getUsername(){return username;}
    public int getCounter(){return counter;}
    public int getDifficulty(){return difficulty;}
    public void setDifficulty(int d){
        difficulty = d;
    }
}
