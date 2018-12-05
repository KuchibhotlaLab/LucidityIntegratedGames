package com.lucidity.game;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url to add a user
    private static String url_add_user = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                // Code here executes on main thread after user presses button
                // Checks that all fields are valid
                boolean valid = validEntry();
                if(valid){
                    //Check for internet connection then attempt to register new user
                    ConnectivityChecker checker = ConnectivityChecker.getInstance(RegisterActivity.this);
                    if (checker.isConnected()){
                        // Adding new user in background thread
                        new AddNewUser().execute();
                    } else {
                        checker.displayNoConnectionDialog();
                    }
                }
            }
        });
    }

    private boolean validEntry(){
        //TODO: check that they are all valid (i.e. not already existent in the database)
        EditText username = (EditText)findViewById(R.id.signup_input_username);
        EditText name = (EditText)findViewById(R.id.signup_input_name);
        EditText pw1 = (EditText)findViewById(R.id.signup_input_password);
        EditText pw2 = (EditText)findViewById(R.id.signup_input_reconfirm_password);
        EditText caregiverpw = (EditText)findViewById(R.id.signup_input_caregiver_password);
        return usernameEntered(username) &&
                passwordConfirmed(pw1, pw2) &&
                caregiverpwConfirmed(caregiverpw) &&
                nameEntered(name);
    }


    private boolean nameEntered(EditText name){
        if(name.getText().toString().trim().length() == 0){
            name.setError("Please Enter Name");
            return false;
        }
        return true;
    }

    private boolean usernameEntered(EditText username){
        if(username.getText().toString().trim().length() == 0){
            username.setError("Please Enter Username");
            return false;
        }
        return true;
    }

    private boolean caregiverpwConfirmed(EditText caregiverpw){
        if(caregiverpw.getText().toString().trim().length() == 0){
            caregiverpw.setError("Please Enter Name");
            return false;
        }
        return true;
    }

    private boolean passwordConfirmed(EditText pw1, EditText pw2){
        if(pw1.getText().toString().trim().length() == 0){
            pw1.setError("Please Enter Password");
            return false;
        }
        if(pw2.getText().toString().trim().length() == 0){
            pw2.setError("Please Enter Password Again");
            return false;
        }
        if(!pw1.getText().toString().equals(pw2.getText().toString())){
            pw2.setError("Please make sure the two passwords are the same");
            return false;
        }
        return true;
    }

    /**
     * Background Async Task to Add new User
     * */
    class AddNewUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Adding User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Adding User
         * */
        protected String doInBackground(String... args) {
            EditText n = (EditText)findViewById(R.id.signup_input_name);
            EditText uname = (EditText)findViewById(R.id.signup_input_username);
            EditText pword = (EditText)findViewById(R.id.signup_input_password);
            EditText carepwd = (EditText)findViewById(R.id.signup_input_caregiver_password);
            String name = n.getText().toString();
            String username = uname.getText().toString();
            String password = pword.getText().toString();
            String caregiverpassword = carepwd.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("caregiverpassword", caregiverpassword));

            // getting JSON Object
            // Note that add user url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_add_user,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {

                    // remember log in status for future
                    Login login = new Login(getApplicationContext());
                    login.newLogin(username, name);

                    // successfully added user
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // closing this screen
                    finish();
                } else {
                    setError(uname, msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    private void setError(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setError(value);
            }
        });
    }
}
