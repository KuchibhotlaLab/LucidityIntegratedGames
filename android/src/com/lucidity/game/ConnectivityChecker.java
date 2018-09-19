package com.lucidity.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChecker {

    static Context context;

    public static ConnectivityChecker getInstance(Context c) {
        context = c;
        return new ConnectivityChecker();
    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isAvailable() &&
                    activeNetwork.isConnected();

        } catch (Exception e) {
            System.out.println("isConnected: " + e.getMessage());
        }
        return false;
    }

    public void displayNoConnectionDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Unable to Connect to Server");
        dialogBuilder.setMessage("An error has occured. Please check your internet connection and then try again.");
        dialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
