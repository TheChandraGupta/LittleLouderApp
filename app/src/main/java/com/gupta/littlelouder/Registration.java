package com.gupta.littlelouder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Registration extends Activity {

    EditText registration_name, registration_email, registration_phone, registration_password1, registration_password2;
    TextView registration_olduser, registration_forget;
    Button registeration_button;
    String userName, userEmail, userPhone, userPassword;

    ProgressDialog dialog;

    boolean status = false;

    User user = new User();

    String TAG = "Registration _Activity:";

    ServerConnection server = new ServerConnection();

    //String URL = "http://localhost:8088/LittleLouderServices/";
    String URL1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/signup";
    String URL = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/signup";
    // ?name=asd&email=asdddd@asd.com&phone=9876543210&password=12345678


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Components of Login Activity
        registeration_button = (Button) findViewById(R.id.registration_button);
        registration_olduser = (TextView) findViewById(R.id.registration_olduser);
        registration_forget = (TextView) findViewById(R.id.registration_forget);
        registration_name = (EditText) findViewById(R.id.registration_name);
        registration_email = (EditText) findViewById(R.id.registration_email);
        registration_phone = (EditText) findViewById(R.id.registration_phone);
        registration_password1 = (EditText) findViewById(R.id.registration_password1);
        registration_password2 = (EditText) findViewById(R.id.registration_password2);

        // Initialize the Dialogue BOX
        dialog = new ProgressDialog(Registration.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        // For Old User Transition
        registration_olduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        // For Forget Password Transition
        registration_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Forget.class));
            }
        });


        registeration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the message onClick of Login Button
                dialog.show();

                userName = registration_name.getText().toString();
                userEmail = registration_email.getText().toString();
                userPhone = registration_phone.getText().toString();
                userPassword = registration_password1.getText().toString();

                try {
                    URL = URL1 + "?name="+ URLEncoder.encode(userName, "utf-8")+"&email="+userEmail+"&phone="+userPhone+"&password="+userPassword;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                new RegistrationDown().execute(URL);

            }
        });

    }

    // Server Coonection and Request/Response transaction block
    public String registrationWS(String url) {

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

                Toast.makeText(Registration.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();

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

    class RegistrationDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return registrationWS(URL);

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
                        DBHandler db = new DBHandler(Registration.this, null, null, 1);
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

                            dialog.hide();

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

                        }

                    }
                    else {

                        dialog.hide();

                        Toast.makeText(Registration.this, "USER ALREADY EXIST, PLEASE REGISTER WITH OTHER EMAIL ID", Toast.LENGTH_SHORT).show();
                        registration_email.setText("");
                        registration_password1.setText("");
                        registration_password2.setText("");
                        return;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
