package com.gupta.littlelouder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AdminChangePassword extends AppCompatActivity {

    DBHandler db = new DBHandler(AdminChangePassword.this, null, null, 1);

    Bundle userBundle;

    ProgressDialog dialog;

    boolean status = false;

    String oldPass, newPass1, newPass2;

    User user = new User();

    String TAG = "AdminChangePassword_Activity:";

    ServerConnection server = new ServerConnection();

    //String URL = "http://localhost:8088/LittleLouderServices/user/login";
    String URL = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/changepassword";
    String URL1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/user/changepassword";
    //?userid=4&password=12345678

    EditText admin_change_password_old_paasowrd, admin_change_password_new_paasowrd1, admin_change_password_new_paasowrd2;
    Button admin_change_password_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_password);

        // Intialize the component of the Activity
        admin_change_password_button = (Button) findViewById(R.id.user_change_password_button);
        admin_change_password_old_paasowrd = (EditText) findViewById(R.id.user_change_password_old_paasowrd);
        admin_change_password_new_paasowrd1 = (EditText) findViewById(R.id.user_change_password_new_paasowrd1);
        admin_change_password_new_paasowrd2 = (EditText) findViewById(R.id.user_change_password_new_paasowrd2);

        // Reeive the user detail bundle from the login Page or Cover Page
        Intent receive = getIntent();
        userBundle = receive.getExtras();

        // Initialize the Dialogue BOX
        dialog = new ProgressDialog(AdminChangePassword.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        // Fetch the user details from the userBundle
        user.setUserId(userBundle.getInt("userId"));
        user.setName(userBundle.getString("name"));
        user.setEmail(userBundle.getString("email"));
        user.setPhone(userBundle.getString("phone"));
        user.setPassword(userBundle.getString("password"));
        user.setType(userBundle.getString("type"));
        user.setdOJ(userBundle.getString("doj"));
        user.setRemember(userBundle.getString("remember"));

        admin_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

                oldPass = admin_change_password_old_paasowrd.getText().toString();

                if (oldPass.equals(user.getPassword())) {

                    newPass1 = admin_change_password_new_paasowrd1.getText().toString();
                    newPass2 = admin_change_password_new_paasowrd2.getText().toString();

                    URL = URL1 + "?userid="+user.getUserId()+"&password="+newPass1;

                    new AdminChangePasswordDown().execute(URL);

                }
                else {

                    Toast.makeText(AdminChangePassword.this, "OLD PASSWORD DID NOT MATCHED", Toast.LENGTH_SHORT).show();

                    admin_change_password_old_paasowrd.setText("");
                    admin_change_password_new_paasowrd1.setText("");
                    admin_change_password_new_paasowrd2.setText("");

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            Intent nextActivity = new Intent(this, AdminHome.class);
            nextActivity.putExtras(userBundle);
            startActivity(nextActivity);
        }
        else if (id == R.id.change_password) {
            Intent nextActivity = new Intent(this, AdminChangePassword.class);
            nextActivity.putExtras(userBundle);
            startActivity(nextActivity);
        }
        else if (id == R.id.logout) {

            db.deleteUser(userBundle.getInt("userId"));

            Intent nextActivity = new Intent(this, Login.class);
            startActivity(nextActivity);
            finish();
        }
        else if (id == R.id.newpost) {
            Intent nextActivity = new Intent(this, AdminNewPost.class);
            nextActivity.putExtras(userBundle);
            startActivity(nextActivity);
        }

        return super.onOptionsItemSelected(item);
    }



    public String adminChangePasswordWS(String url) {

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

                Log.d(TAG, "Success to Download File from URL-" + url);

            }
            else {

                Toast.makeText(AdminChangePassword.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();

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

    class AdminChangePasswordDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return adminChangePasswordWS(URL); //***************************************************************************

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

                        user.setPassword(object.getString("password"));

                        Bundle sendBundle = new Bundle();

                        sendBundle.putInt("userId", user.getUserId());
                        sendBundle.putString("name", user.getName());
                        sendBundle.putString("email", user.getEmail());
                        sendBundle.putString("phone", user.getPhone());
                        sendBundle.putString("password", user.getPassword());
                        sendBundle.putString("type", user.getType());
                        sendBundle.putString("doj", user.getdOJ());
                        sendBundle.putString("remember", user.getRemember());


                        dialog.hide();

                        Toast.makeText(AdminChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();

                        // Create intent for movinf to new Activity
                        Intent loginIntent = new Intent(getApplicationContext(), AdminHome.class);
                        // Add Bundle to intent
                        loginIntent.putExtras(sendBundle);
                        // Start the next Activity
                        startActivity(loginIntent);
                        // Finish the current Activity
                        //finish();

                    }
                    else {

                        dialog.hide();

                        Toast.makeText(AdminChangePassword.this, "Password Didn't Changed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
