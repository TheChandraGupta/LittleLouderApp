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

public class Forget extends Activity {

    Button forget_button;
    EditText forget_email;
    TextView forget_newuser,forget_olduser;

    String userEmail;

    ProgressDialog dialog;

    String KEY, CODE;

    User user = new User();

    String TAG = "Forget_Password_Activity:";

    ServerConnection server = new ServerConnection();

    //String URL = "http://localhost:8088/LittleLouderServices/";
    String URL1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/forget";
    String URL = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/forget";
    // ?email=asdd@asd.com

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        // Components of Login Activity
        forget_email = (EditText) findViewById(R.id.forget_email);
        forget_button = (Button) findViewById(R.id.forget_button);
        forget_newuser = (TextView) findViewById(R.id.forget_newuser);
        forget_olduser = (TextView) findViewById(R.id.forget_olduser);

        // Initialize the Dialogue BOX
        dialog = new ProgressDialog(Forget.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);


        // For New User Transition
        forget_newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        });

        // For Forget Password Transition
        forget_olduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        forget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the message onClick of Login Button
                dialog.show();

                userEmail = forget_email.getText().toString();

                URL = URL1 + "?email="+userEmail;

                new ForgetPasswordDown().execute(URL);

            }
        });

    }

    // Server Coonection and Request/Response transaction block
    public String forgetPasswordWS(String url) {

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

                Toast.makeText(Forget.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();

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

    class ForgetPasswordDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return forgetPasswordWS(URL);

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    KEY = object.getString("KEY");
                    CODE = object.getString("CODE");

                    if (CODE.equals("true")) {

                        dialog.hide();

                        Toast.makeText(Forget.this, "YOUR PASSWORD HAS BEEN RECOVERED, PLEASE CHECK YOUR MAIL", Toast.LENGTH_SHORT).show();

                        // Create intent for movinf to new Activity
                        Intent loginIntent = new Intent(getApplicationContext(), Login.class);

                        // Start the next Activity
                        startActivity(loginIntent);

                        // Finish the current Activity
                        //finish();

                    }
                    else {

                        dialog.hide();

                        Toast.makeText(Forget.this, "INVALID EMAIL ID", Toast.LENGTH_SHORT).show();
                        forget_email.setText("");
                        return;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
