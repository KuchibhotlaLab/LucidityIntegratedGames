package com.lucidity.game;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryQuestionnaireActivity extends AppCompatActivity {
    //Stores the username of the user
    private String username;
    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url to add an event to personal history
    private static String url_add_event = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_history.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private ArrayList<String[]> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_questionnaire);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        Button btn_submit = findViewById(R.id.history_submit_button);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                responses = new ArrayList<>();

                EditText answer1_1 = findViewById(R.id.history_answer_1_1);
                EditText answer1_2 = findViewById(R.id.history_answer_1_2);
                if(answer1_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer1_1.getText().toString(), "Birth", answer1_2.getText().toString()});
                }

                EditText answer2_1 = findViewById(R.id.history_answer_2_1);
                EditText answer2_2 = findViewById(R.id.history_answer_2_2);
                if(answer2_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer2_1.getText().toString(), "Primary School", answer2_2.getText().toString()});
                }

                EditText answer3_1 = findViewById(R.id.history_answer_3_1);
                EditText answer3_2 = findViewById(R.id.history_answer_3_2);
                if(answer3_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer3_1.getText().toString(), "High School", answer3_2.getText().toString()});
                }

                EditText answer4_1 = findViewById(R.id.history_answer_4_1);
                EditText answer4_2 = findViewById(R.id.history_answer_4_2);
                if(answer4_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer4_1.getText().toString(), "College", answer4_2.getText().toString()});
                }

                EditText answer5_1 = findViewById(R.id.history_answer_5_1);
                EditText answer5_2 = findViewById(R.id.history_answer_5_2);
                if(answer5_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer5_1.getText().toString(), "First Job", answer5_2.getText().toString()});
                }

                EditText answer6_1 = findViewById(R.id.history_answer_6_1);
                EditText answer6_2 = findViewById(R.id.history_answer_6_2);
                if(answer6_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer6_1.getText().toString(), "Wedding", answer6_2.getText().toString()});
                }

                EditText answer7_1 = findViewById(R.id.history_answer_7_1);
                EditText answer7_2 = findViewById(R.id.history_answer_7_2);
                if(answer7_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer7_1.getText().toString(), "First Child", answer7_2.getText().toString()});
                }

                EditText answer8_1 = findViewById(R.id.history_answer_8_1);
                EditText answer8_2 = findViewById(R.id.history_answer_8_2);
                if(answer8_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer8_1.getText().toString(), "Lived Here", answer8_2.getText().toString()});
                }

                EditText answer9_1 = findViewById(R.id.history_answer_9_1);
                EditText answer9_2 = findViewById(R.id.history_answer_9_2);
                if(answer9_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer9_1.getText().toString(), "Lived Here", answer9_2.getText().toString()});
                }

                EditText answer10_1 = findViewById(R.id.history_answer_10_1);
                EditText answer10_2 = findViewById(R.id.history_answer_10_2);
                if(answer10_1.getText().toString().trim().length() != 0) {
                    responses.add(new String[]{answer10_1.getText().toString(), "Lived Here", answer10_2.getText().toString()});
                }

                System.out.println(responses.get(0)[0] + " " + responses.get(0)[1] + " " + responses.get(0)[2]);
                System.out.println(responses.get(2)[0] + " " + responses.get(2)[1] + " " + responses.get(2)[2]);

                ConnectivityChecker checker = ConnectivityChecker.getInstance(HistoryQuestionnaireActivity.this);
                if (checker.isConnected()){
                    //Add locations and events based on questionnaire
                    new AddEvents().execute();
                } else {
                    checker.displayNoConnectionDialog();
                }
            }
        });
    }

    /**
     * Background Async Task to Add new Events
     * */
    class AddEvents extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HistoryQuestionnaireActivity.this);
            pDialog.setMessage("Filling out Histories..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Adding Histories
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params;

            LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Histories")
                    .build();
            HistoryDAO historyDAO = database.getHistoryDAO();

            for (String[] event : responses) {
                params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("location", event[0]));
                params.add(new BasicNameValuePair("event", event[1]));
                params.add(new BasicNameValuePair("year", event[2]));

                // getting JSON Object
                JSONObject json = jsonParser.makeHttpRequest(url_add_event,
                        "POST", params);

                // check log cat for response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    String msg = json.getString(TAG_MESSAGE);

                    if (success == 1) {
                        Log.d("Check Event Added", "Success");
                        //save local
                        History history = new History();
                        history.setUsername(username);
                        history.setLocation(event[0]);
                        history.setEvent(event[1]);
                        history.setYear(event[2]);

                        historyDAO.insert(history);

                    } else {
                        Log.d("Check Event Added", msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            database.close();
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            finish();
        }
    }
}
