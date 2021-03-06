package com.lucidity.game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    //Stores the username of the user
    private String username;

    //Stores the name of the user
    private String name;

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    TextView nameDisplay;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    // url to check caregiver password
    private static String url_verify_carepass = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/verify_carepass.php";

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
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                showDialog();
            }
        });


        Button main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                // Goes to home page activity for patient
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
            }
        });

        Button btnGame = findViewById(R.id.lucid);
        btnGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                final String currentDateTimeString = dateFormat.format(date);
                final FullTestGenerator gen = new FullTestGenerator();
                System.out.println(gen);

                new Thread(new Runnable() {
                    public void run() {
                        //Start saving an object to represent the full test
                        LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-FullTestRuns")
                                .build();
                        FullTestRunDAO fullTestRunDAO = database.getFullTestRunDAO();

                        FullTestRun testRun = new FullTestRun();
                        testRun.setUsername(username);
                        testRun.setTime(currentDateTimeString);
                        testRun.setMenu("Patient");
                        testRun.setPicture("");
                        testRun.setTesttype1(gen.getGameType(Integer.parseInt(gen.toString().substring(0,1))));
                        testRun.setTesttype2(gen.getGameType(Integer.parseInt(gen.toString().substring(1,2))));
                        testRun.setTesttype3(gen.getGameType(Integer.parseInt(gen.toString().substring(2,3))));
                        testRun.setTesttype4(gen.getGameType(Integer.parseInt(gen.toString().substring(3,4))));
                        testRun.setTesttime1("");
                        testRun.setTesttime2("");
                        testRun.setTesttime3("");
                        testRun.setTesttime4("");
                        fullTestRunDAO.insert(testRun);
                        database.close();
                    }
                }).start();

                Intent i = new Intent(getApplicationContext(), AndroidLauncher.class);
                i.putExtra("isLucid", true);
                i.putExtra("isCare", false);
                i.putExtra("isPatient", false);
                i.putExtra("gametype", "check order");
                i.putExtra("order", gen.toString());
                i.putExtra("counter", 0);
                i.putExtra("difficulty", -1);
                i.putExtra("startTime", currentDateTimeString);

                startActivity(i);
            }
        });

        FloatingActionButton notif = findViewById(R.id.button_notification);
        notif.setVisibility(View.GONE);
        TextView notifText = findViewById(R.id.text_notif);
        notifText.setVisibility(View.GONE);
        CheckScores checkScoresTask = new CheckScores();
        checkScoresTask.execute();
    }

    @Override
    public void onResume(){
        super.onResume();
        CheckScores checkScoresTask = new CheckScores();
        checkScoresTask.execute();
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
                .setPositiveButton("Verify",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // DO THE METHOD HERE WHEN PROCEED IS CLICKED
                                String user_text = (userInput.getText()).toString();

                                if (user_text.trim().length() == 0) {
                                    userInput.setError("Password is incorrect");
                                }
                            }
                        })
                .setNegativeButton("Cancel",
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
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

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
                    //Check for internet connection first
                    ConnectivityChecker checker = ConnectivityChecker.getInstance(MainActivity.this);
                    if (checker.isConnected()){
                        verifyTask.execute();
                    } else {
                        checker.displayNoConnectionDialog();
                    }
                }

                if(succeed[0] == 1)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    /**
     * Background Async Task to check if scores need to be uploaded
     * */
    class CheckScores extends AsyncTask<String, String, String> {
        private boolean areScoresUploaded;

        protected String doInBackground(String... args) {
            areScoresUploaded = true;

            LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-BlockGameScores")
                    .build();
            BlockGameScoreDAO blockGameScoreDAO = database.getBlockGameScoreDAO();
            List<BlockGameScore> blockGameScoreList = blockGameScoreDAO.getUserBlockGameScores(username);
            database.close();
            if(!blockGameScoreList.isEmpty()) {
                areScoresUploaded = false;
                return null;
            }

            database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ObjGameScores")
                    .build();
            ObjGameScoreDAO objGameScoreDAO = database.getObjGameScoreDAO();
            List<ObjGameScore> objGameScoreList = objGameScoreDAO.getUserObjGameScores(username);
            database.close();
            if(!objGameScoreList.isEmpty()) {
                areScoresUploaded = false;
                return null;
            }

            database = Room.databaseBuilder(context, LucidityDatabase.class, "db-SpGameScores")
                    .build();
            SpGameScoreDAO spGameScoreDAO = database.getSpGameScoreDAO();
            List<SpGameScore> spGameScoreList = spGameScoreDAO.getUserSpGameScores(username);
            database.close();
            if(!spGameScoreList.isEmpty()) {
                areScoresUploaded = false;
                return null;
            }

            database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FtNGameScores")
                    .build();
            FtNGameScoreDAO ftnGameScoreDAO = database.getFtNGameScoreDAO();
            List<FtNGameScore> ftnGameScoreList = ftnGameScoreDAO.getUserFtNGameScores(username);
            database.close();
            if(!ftnGameScoreList.isEmpty()) {
                areScoresUploaded = false;
                return null;
            }

            database = Room.databaseBuilder(context, LucidityDatabase.class, "db-NtFGameScores")
                    .build();
            NtFGameScoreDAO ntfGameScoreDAO = database.getNtFGameScoreDAO();
            List<NtFGameScore> ntfGameScoreList = ntfGameScoreDAO.getUserNtFGameScores(username);
            database.close();
            if(!ntfGameScoreList.isEmpty()) {
                areScoresUploaded = false;
                return null;
            }

            database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ReGameScores")
                    .build();
            ReGameScoreDAO reGameScoreDAO = database.getReGameScoreDAO();
            List<ReGameScore> reGameScoreList = reGameScoreDAO.getUserReGameScores(username);
            database.close();
            if(!reGameScoreList.isEmpty()) {
                areScoresUploaded = false;
                return null;
            }

            database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FullTestRuns")
                    .build();
            FullTestRunDAO fullTestRunDAO = database.getFullTestRunDAO();

            List<FullTestRun> fullTestRuns = fullTestRunDAO.getUserTestSuiteRuns(username);
            for (FullTestRun f: fullTestRuns) {
                if (!f.getTesttime1().isEmpty()) {
                    areScoresUploaded = false;
                    return null;
                } else {
                    fullTestRunDAO.delete(f);
                }
            }
            database.close();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            final FloatingActionButton notif = findViewById(R.id.button_notification);
            final TextView notifText = findViewById(R.id.text_notif);

            if (areScoresUploaded == false) {
                notif.setVisibility(View.VISIBLE);
                notifText.setVisibility(View.VISIBLE);
                notif.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder
                                .setMessage("You have game results that have not been uploaded online yet. Press OK to upload them now.")
                                .setCancelable(false)
                                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Check for internet connection first
                                        ConnectivityChecker checker = ConnectivityChecker.getInstance(MainActivity.this);
                                        if (checker.isConnected()){
                                            new Thread(new Runnable() {
                                                public void run() {
                                                    // post locally saved scores to web server if there are any.
                                                    ScorePosterAndroid poster = new ScorePosterAndroid(getApplicationContext());
                                                    poster.postOnline(username);
                                                    poster.postSuiteOnline(username);
                                                }
                                            }).start();
                                            notif.setVisibility(View.GONE);
                                            notifText.setVisibility(View.GONE);
                                        } else {
                                            checker.displayNoConnectionDialog();
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            } else {
                notif.setVisibility(View.GONE);
                notifText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Background Async Task to verify the caregiver password
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
                    // post locally saved scores to web server if there are any.
                    ScorePosterAndroid poster = new ScorePosterAndroid(getApplicationContext());
                    poster.postOnline(username);
                    poster.postSuiteOnline(username);
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

     class LoadGameTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private Intent intent;

        public LoadGameTask(Intent i) {
            this.intent = i;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            progressDialog.setMessage("Loading Game");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            startActivity(intent);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();


        }
    }


}

//stackoverflow.com/questions/2620444/
interface AsyncResponse {
    void processFinish(int output);
}