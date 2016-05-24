package com.gupta.littlelouder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gupta.littlelouder.bean.Comment;
import com.gupta.littlelouder.bean.User;
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
import java.util.ArrayList;

public class CommentPopUp extends Activity {

    ArrayList<Comment> allComment = new ArrayList<Comment>();

    TextView comment_notification_message;
    EditText comment_new_comment_text;
    Button comment_new_comment_button;
    ListView comment_all_comment_list;

    Bundle userBundle;

    ProgressDialog dialog;

    //boolean status = false;

    String message, postId;

    User user = new User();

    String TAG = "CommentPopUp_Activity:";

    ServerConnection server = new ServerConnection();

    //String URL = "http://localhost:8088/LittleLouderServices/user/login";
    String URL_NEW_COMMENT = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/new";
    String URL_NEW_COMMENT1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/new";
    //  ?userid=11&postid=4&message=New Test 1

    //String URL = "http://localhost:8088/LittleLouderServices/user/login";
    String URL_ALL_COMMENT = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/all";
    String URL_ALL_COMMENT1 = "http://" + server.getIP() + ":" + server.getPORT() + "/" + server.getAPP() + "/comment/all";
    //  ?postid=41

    String notification = "No Comments Found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.7));

        // Intialize the component of the Activity
        comment_notification_message = (TextView) findViewById(R.id.comment_notification_message);
        comment_new_comment_text = (EditText) findViewById(R.id.comment_new_comment_text);
        comment_new_comment_button = (Button) findViewById(R.id.comment_new_comment_button);
        comment_all_comment_list = (ListView) findViewById(R.id.comment_all_comment_list);

        comment_notification_message.setVisibility(View.GONE);

        // Receive the user detail bundle from the Home Page
        Intent receive = getIntent();
        userBundle = receive.getExtras();

        // Initialize the Dialogue BOX
        dialog = new ProgressDialog(CommentPopUp.this);
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

        postId  = "" + userBundle.getInt("postId");

        dialog.show();

        URL_ALL_COMMENT = URL_ALL_COMMENT1 + "?postid="+postId;

        new AllNewCommentPopUpDown().execute(URL_ALL_COMMENT);

        comment_new_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

                message = comment_new_comment_text.getText().toString();

                try {
                    URL_NEW_COMMENT = URL_NEW_COMMENT1 + "?userid="+user.getUserId()+"&postid="+postId+"&message="+ URLEncoder.encode(message, "utf-8");

                    new AllNewCommentPopUpDown().execute(URL_NEW_COMMENT);

                } catch (UnsupportedEncodingException e) {
                    dialog.hide();
                    e.printStackTrace();
                }

            }
        });


    }



    public String commentPopUpWS(String url) {

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

                Toast.makeText(CommentPopUp.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();

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

    class AllNewCommentPopUpDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // display the progress dialogue
            dialog.show();

            String URL = params[0];

            // execute the server login validation and return to the on PostExecution
            return commentPopUpWS(URL); //***************************************************************************

        }

        // This method will be executed after the fetching of data from the Web Services
        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray array = new JSONArray(result);

                if (array.length() > 0) {

                    String[] commentMessage = new String[array.length()];

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        Comment comment = new Comment();

                        comment.setCommentId(object.getInt("commentId"));
                        comment.setMessage(object.getString("message"));
                        comment.setUserId(object.getInt("userId"));
                        comment.setPostId(object.getInt("postId"));
                        comment.setDate(object.getString("date"));
                        comment.setUserName(object.getString("userName"));

                        commentMessage[i] = comment.getMessage();

                        allComment.add(comment);

                    }

                    comment_notification_message.setVisibility(View.GONE);
                    comment_all_comment_list.setVisibility(View.VISIBLE);

                    ListAdapter allCommentListAdapter = new ArrayAdapter<String>(CommentPopUp.this, android.R.layout.simple_list_item_1, commentMessage);
                    comment_all_comment_list.setAdapter(allCommentListAdapter);

                }
                else {

                    dialog.hide();

                    comment_all_comment_list.setVisibility(View.GONE);
                    notification = "No Comments Found";
                    comment_notification_message.setText(notification);
                    comment_notification_message.setVisibility(View.VISIBLE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}
