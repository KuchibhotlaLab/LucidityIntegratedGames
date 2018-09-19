package com.lucidity.game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    //Stores the username of the user
    private String username;

    //Stores the name of the user
    private String name;

    TextView nameDisplay;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    // url to check caregiver password
    private static String url_verify_carepass = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/verify_carepass.php";

    // url to get a user's name
    private static String url_get_name = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_name.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets username and name from shared preferences
        Login login = new Login(getApplicationContext());
        username = login.getUsername();
        name = login.getName();

        // display name at top of page
        nameDisplay = (TextView) findViewById(R.id.name_display);
        nameDisplay.setText("Hello, " + name);

        //Pop up menu for logging out
        final Button btnMenu = findViewById(R.id.button_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(MainActivity.this,btnMenu);
                menu.getMenuInflater().inflate(R.menu.menu_popup, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.logout:
                                Login login = new Login(getApplicationContext());
                                login.logout();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                menu.show();
            }
        });

        Button btnCaregiver = findViewById(R.id.caregiver);
        btnCaregiver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();

            }
        });


        Button main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Goes to home page activity for patient
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
            }
        });

        Button btnGame = findViewById(R.id.lucid);
        btnGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                FullTestGenerator gen = new FullTestGenerator();

                Intent i = new Intent(getApplicationContext(), AndroidLauncher.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", true);
                i.putExtra("isCare", false);
                i.putExtra("isPatient", false);
                i.putExtra("gametype", "check order");
                i.putExtra("order", gen.toString());
                i.putExtra("counter", 0);
                i.putExtra("difficulty", -1);

                startActivity(i);
            }
        });

    }

    public void showDialog()
    {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.password_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.caregiver_password);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Go",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // DO THE METHOD HERE WHEN PROCEED IS CLICKED
                                String user_text = (userInput.getText()).toString();

                                if (user_text.trim().length() == 0)
                                {
                                    userInput.setError("Password is incorrect");
                                }
                                else{
                                    /*Log.d(user_text,"string is empty");
                                    String message = "The password you have entered is incorrect." + " \n \n" + "Please try again!";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                    builder.setTitle("Error");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Cancel", null);
                                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            showDialog();
                                        }
                                    });
                                    builder.create().show();*/

                                    //VerifyCaregiver verifyTask = new VerifyCaregiver(user_text, userInput);
                                    //verifyTask.execute();
                                }
                            }
                        })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                            }

                        }

                );

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String user_text = (userInput.getText()).toString();
                final int[] succeed = new int[1];
                if (user_text.trim().length() == 0)
                {
                    userInput.setError("Password is incorrect");
                }
                else{
                    VerifyCaregiver verifyTask = new VerifyCaregiver(user_text, userInput, new AsyncResponse() {
                        @Override
                        public void processFinish(int output) {
                            succeed[0] = output;
                        }
                    });
                    verifyTask.execute();
                }


                if(succeed[0] == 1)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

    }

    /**
     * Background Async Task to Verify the caregiver password
     * */
    class VerifyCaregiver extends AsyncTask<String, String, String> {

        private String inputText;
        private EditText inputTextObject;
        private int success;

        public AsyncResponse delegate = null;



        public VerifyCaregiver(String userText, EditText userInput, AsyncResponse delegate)
        {
            inputText = userText;
            inputTextObject = userInput;
            this.delegate = delegate;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Verifying caregiver password..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Login
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("caregiverpassword", inputText));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_verify_carepass,
                    "GET", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    // successfully verified password
                    Intent intent = new Intent(getApplicationContext(), CaregiverHomePage.class);
                    startActivity(intent);
                } else {
                    setError(inputTextObject, msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        /*protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }*/



        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            delegate.processFinish(success);
        }

    }

    private void setError(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setError(value);
                System.out.println("reached");
            }
        });
    }


}

//stackoverflow.com/questions/2620444/
interface AsyncResponse {
    void processFinish(int output);
}