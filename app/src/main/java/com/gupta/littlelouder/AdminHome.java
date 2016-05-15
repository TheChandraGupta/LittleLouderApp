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
import android.widget.TextView;
import android.widget.Toast;

import com.gupta.littlelouder.bean.Post;
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

public class AdminHome extends AppCompatActivity {

    DBHandler db = new DBHandler(AdminHome.this, null, null, 1);

    Bundle userBundle;

    ProgressDialog dialog;

    boolean status = false;

    User user = new User();
    Post post = new Post();

    String TAG = "AdminHome_Activity:";

    ServerConnection server = new ServerConnection();

    //String URL = "http://localhost:8088/LittleLouderServices/user/login";
    String URL_POST_LATEST = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/post/latest";
    String URL_POST_LATEST1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/post/latest";
    //?userid=4

    String URL_POST_LIKE = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/post/like";
    String URL_POST_LIKE1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/post/like";
    //?userid=4&postid=4

    String URL_POST_DISLIKE = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/post/dislike";
    String URL_POST_DISLIKE1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/post/dislike";
    //?userid=4&postid=4

    String URL_POST_COMMENT_ALL = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/all";
    String URL_POST_COMMENT_ALL1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/all";
    //?postid=4

    String URL_POST_COMMENT_NEW = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/new";
    String URL_POST_COMMENT_NEW1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/new";
    // ?userid=4&postid=4&message=message 17

    // Components of User Home Activity
    TextView admin_home_post, admin_home_like, admin_home_comment, admin_home_like_count, admin_home_comment_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize the component of the Activity
        admin_home_post = (TextView) findViewById(R.id.admin_home_post);
        admin_home_like = (TextView) findViewById(R.id.admin_home_like);
        admin_home_comment = (TextView) findViewById(R.id.admin_home_comment);
        admin_home_like_count = (TextView) findViewById(R.id.admin_home_like_count);
        admin_home_comment_count = (TextView) findViewById(R.id.admin_home_comment_count);

        // Receive the user detail bundle from the login Page or Cover Page
        Intent receive = getIntent();
        userBundle = receive.getExtras();

        // Initialize the Dialogue BOX
        dialog = new ProgressDialog(AdminHome.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        // Show the message on Load
        dialog.show();

        // Fetch the user details from the userBundle
        user.setUserId(userBundle.getInt("userId"));
        user.setName(userBundle.getString("name"));
        user.setEmail(userBundle.getString("email"));
        user.setPhone(userBundle.getString("phone"));
        user.setPassword(userBundle.getString("password"));
        user.setType(userBundle.getString("type"));
        user.setdOJ(userBundle.getString("doj"));
        user.setRemember(userBundle.getString("remember"));

        // Fetch the Latest Post
        URL_POST_LATEST = URL_POST_LATEST1 + "?userid=" + user.getUserId();
        new UserHomePostDown().execute(URL_POST_LATEST);

        admin_home_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                //check whether it is already checked or not
                if (post.isLike()) {
                    // this block states that user has already pressed like before, so now do dislike
                    admin_home_like.setTextColor(getResources().getColor(R.color.white));

                    // Perform the Dislike Operation
                    URL_POST_DISLIKE = URL_POST_DISLIKE1 + "?userid=" + user.getUserId() + "&postid=" + post.getPostId();
                    new UserHomeDisLikeDown().execute(URL_POST_DISLIKE);
                }
                else {
                    // perform the like operation

                    // this block states that user has already pressed like before, so now do dislike
                    admin_home_like.setTextColor(getResources().getColor(R.color.red));

                    // Perform the Like Operation
                    URL_POST_LIKE = URL_POST_LIKE1 + "?userid=" + user.getUserId() + "&postid=" + post.getPostId();
                    new UserHomeLikeDown().execute(URL_POST_LIKE);
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


    //*******************************************************************************************************************..

    /*

    Latest Post
    Like
    Dislike
    New Comment
    All Comment

     */
    public String userHomeWS(String url) {

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

                Toast.makeText(AdminHome.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();

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

    class UserHomePostDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return userHomeWS(URL_POST_LATEST); //***************************************************************************

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

                        post.setPostId(object.getInt("postid"));
                        post.setPost(object.getString("post"));
                        post.setUpVote(object.getInt("upvote"));
                        post.setDownVote(object.getInt("downvote"));
                        post.setUserId(object.getInt("userid"));
                        post.setDate(object.getString("date"));
                        post.setLike(object.getBoolean("like"));

                        dialog.hide();

                        admin_home_post.setText(post.getPost());
                        admin_home_like_count.setText("" + post.getUpVote());

                        if (post.isLike()) {
                            admin_home_like.setTextColor(getResources().getColor(R.color.red));
                        }
                        else {
                            admin_home_like.setTextColor(getResources().getColor(R.color.white));
                        }

                    }
                    else {

                        dialog.hide();

                        Toast.makeText(AdminHome.this, "Unable to Load Data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class UserHomeLikeDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return userHomeWS(URL_POST_LIKE); //***************************************************************************

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    post.setUpVote(object.getInt("upvote"));
                    post.setLike(true);

                    dialog.hide();

                    admin_home_like_count.setText("" + post.getUpVote());
                    admin_home_like.setTextColor(getResources().getColor(R.color.red));


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class UserHomeDisLikeDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return userHomeWS(URL_POST_DISLIKE); //***************************************************************************

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    post.setUpVote(object.getInt("upvote"));
                    post.setLike(false);

                    dialog.hide();

                    admin_home_like_count.setText("" + post.getUpVote());
                    admin_home_like.setTextColor(getResources().getColor(R.color.white));


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class UserHomeCommentNewDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return userHomeWS(URL_POST_COMMENT_NEW); //***************************************************************************

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class UserHomeCommentAllDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();
            // execute the server login validation and return to the on PostExecution
            return userHomeWS(URL_POST_COMMENT_ALL); //***************************************************************************

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
