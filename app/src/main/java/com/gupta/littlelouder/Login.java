package com.gupta.littlelouder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gupta.littlelouder.bean.User;
import com.gupta.littlelouder.database.DBHandler;
import com.gupta.littlelouder.server.ServerConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Login extends Activity {

    Button login_button;
    EditText login_email, login_password;
    TextView login_newuser,login_forgetpassword;

    String userEmail, userPassword;

    ProgressDialog dialog;

    boolean status = false;

    User user = new User();

    String TAG = "Login_Activity:";

    ServerConnection server = new ServerConnection();

    //String URL = "http://localhost:8088/LittleLouderServices/user/login";
    String URL1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/login";
    String URL = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/login";
    //?email=chandra.prakashg01@gmail.com&password=12345678

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Components of Login Activity
        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        login_newuser = (TextView) findViewById(R.id.login_newuser);
        login_forgetpassword = (TextView) findViewById(R.id.login_forgetpassword);
        login_button = (Button)findViewById(R.id.login_button);

        // Initialize the Dialogue BOX
        dialog = new ProgressDialog(Login.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);


        // For New User Transition
        login_newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        });

        // For Forget Password Transition
        login_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Forget.class));
            }
        });

        // Login Button onClick Procedure to validate the user from server and store the data in device database
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the message onClick of Login Button
                dialog.show();

                userEmail = login_email.getText().toString();
                userPassword = login_password.getText().toString();

                URL = URL1 + "?email="+userEmail+"&password="+userPassword;

                // Request server for login Validation and get the user details
                new LoginDown().execute();

                // If the user credentials are correct
                if (status) {
/*
                    // Create DBHandle object to perform CRUD operation
                    DBHandler db = new DBHandler(Login.this, null, null, 1);
                    // Add the user details to the database for fast and easy login in future
                    db.addNewUser(user);

                    if (user.getType().equals("USER")) {

                        // Create a Bundle of User detail to pass between the pages.
                        Bundle userBundle = new Bundle();

                        // Add dtails to the Bundle
                        userBundle.putInt("userId", user.getUserId());
                        userBundle.putString("name", user.getName());
                        userBundle.putString("email", user.getEmail());
                        userBundle.putString("phone", user.getPhone());
                        userBundle.putString("password", user.getPassword());
                        userBundle.putString("type", user.getType());
                        userBundle.putString("doj", user.getdOJ());
                        userBundle.putString("remember", user.getRemember());

                        // Create intent for movinf to new Activity
                        Intent loginIntent = new Intent(getApplicationContext(), UserHome.class);
                        // Add Bundle to intent
                        loginIntent.putExtras(userBundle);
                        // Start the next Activity
                        startActivity(loginIntent);
                        // Finish the current Activity
                        //finish();

                    }
                    else {

                    }*/

                }
                else { // if the Credentials are incorrect
                    /*Toast.makeText(Login.this, "INVALID USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();
                    login_email.setText("");
                    login_password.setText(""); */
                }

            }
        });

    }

    // Server Coonection and Request/Response transaction block
    public String loginValidationWS(String url) {

        // It is use to create the output string for JSON parsing
        StringBuilder stringBuilder = new StringBuilder();
        // Create the connection
        HttpClient client = new DefaultHttpClient();
        // Defines the request and response method ex. POST or GET and sets the URL
        HttpPost post = new HttpPost(url);

        try {

            // Iy is used to get the response from the server
            HttpResponse response = client.execute(post);
            // Get the statusCode. For successful executeion the statusCode is 200 and other are 400, 404, 500 and etc
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {

                // Parse the entity from the reponse
                HttpEntity entity = response.getEntity();
                // Parse the entity into the stream
                InputStream inputStream = entity.getContent();

                // It will store the stream into buffer for simple execute or extraction
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String output;

                // It will add line by line to the output string
                while ((output = reader.readLine()) != null) {
                    stringBuilder.append(output);
                }

                Log.d(TAG, "Success to Download File");

            }
            else {

                Toast.makeText(Login.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Failed to Download File from URL-" + url);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the output data into string and return to the onPostExecution method
        return stringBuilder.toString();

    }

    class LoginDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return loginValidationWS(URL);

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    status = object.getBoolean("status");

                    if (status) {

                        user.setUserId(object.getInt("userId"));
                        user.setName(object.getString("name"));
                        user.setEmail(object.getString("email"));
                        user.setPhone(object.getString("phone"));
                        user.setPassword(object.getString("password"));
                        user.setType(object.getString("type"));
                        user.setdOJ(object.getString("doj"));
                        user.setRemember(object.getString("remember"));

                        // Create DBHandle object to perform CRUD operation
                        DBHandler db = new DBHandler(Login.this, null, null, 1);
                        // Add the user details to the database for fast and easy login in future
                        db.addNewUser(user);

                        // Create a Bundle of User detail to pass between the pages.
                        Bundle userBundle = new Bundle();

                        // Add dtails to the Bundle
                        userBundle.putInt("userId", user.getUserId());
                        userBundle.putString("name", user.getName());
                        userBundle.putString("email", user.getEmail());
                        userBundle.putString("phone", user.getPhone());
                        userBundle.putString("password", user.getPassword());
                        userBundle.putString("type", user.getType());
                        userBundle.putString("doj", user.getdOJ());
                        userBundle.putString("remember", user.getRemember());

                        dialog.hide();


                        if (user.getType().equals("USER")) {
                            
                            // Create intent for movinf to new Activity
                            Intent loginIntent = new Intent(getApplicationContext(), UserHome.class);
                            // Add Bundle to intent
                            loginIntent.putExtras(userBundle);
                            // Start the next Activity
                            startActivity(loginIntent);
                            // Finish the current Activity
                            //finish();

                        }
                        else {

                            // Create intent for movinf to new Activity
                            Intent loginIntent = new Intent(getApplicationContext(), AdminHome.class);
                            // Add Bundle to intent
                            loginIntent.putExtras(userBundle);
                            // Start the next Activity
                            startActivity(loginIntent);
                            // Finish the current Activity
                            //finish();

                        }

                    }
                    else {

                        dialog.hide();

                        Toast.makeText(Login.this, "INVALID USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();
                        login_password.setText("");
                        return;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
