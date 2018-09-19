package com.lucidity.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 *Class used for checking internet connection
 */
public class ConnectivityChecker {

    static Context context;

    public static ConnectivityChecker getInstance(Context c) {
        context = c;
        return new ConnectivityChecker();
    }

    //Check for a network connection and return whether the device is connected or not
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

    //Display a dialog for when there is no connection
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
