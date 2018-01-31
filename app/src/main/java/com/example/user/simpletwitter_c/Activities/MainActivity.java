package com.example.user.simpletwitter_c.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.user.simpletwitter_c.R;
import com.example.user.simpletwitter_c.Utilies.ObjectSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";
    private static final String PREF_USER_Id = "user_id";



    public static final int WEBVIEW_REQUEST_CODE = 100;



    private static twitter4j.Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences sharedPreferences;





    private View loginLayout;


    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // myList=new ArrayList<>();

           initConfigs();

        //StrictMode is a developer tool which detects things you might be-
        // -doing by accident and brings them to your attention so you can fix them.

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        loginLayout = findViewById(R.id.login_layout);

        findViewById(R.id.btn_login).setOnClickListener(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, 0);

        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        // the time you open the app and it's not the first time it will go to the followers-
        // -screen of your account that you registered in first time
        //if not logged before then authticate and get user and id and list of followers and go to scrren followers

        if(isLoggedIn) {


            String username = sharedPreferences.getString(PREF_USER_NAME, "");
            Long userId = sharedPreferences.getLong(PREF_USER_Id, 0);



            try {
                Followers.followers = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("UserList", ObjectSerializer.serialize(new ArrayList())));
            } catch (IOException e) {
                e.printStackTrace();
            }


            Intent intent=new Intent(this,Followers.class);
            intent.putExtra("Namee",username);
            intent.putExtra("twittersession_id",userId);
            startActivity(intent);

        } else {


            Uri uri = getIntent().getData();

            if(uri != null && uri.toString().startsWith(callbackUrl)) {

                String verifier = uri.getQueryParameter(oAuthVerifier);

                try {

                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    long userId = accessToken.getUserId();
                    final User user = twitter.showUser(userId);
                    final String username = user.getName();


                    getFriendList();

                   saveTwitterInfo(accessToken);

                    Intent intent=new Intent(this,Followers.class);
                    intent.putExtra("Namee",username);
                    intent.putExtra("twittersession_id",userId);


                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //get followers using twitter4j library and put the list in followers list which is defined static in followers activity
    public List<User> getFriendList() {
        List<User> friendList = null;
        try {
            friendList = twitter.getFollowersList(twitter.getId(), -1);

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, ""+friendList, Toast.LENGTH_LONG).show();
        if (friendList != null) {
            Followers.followers = new ArrayList<User>();
            Followers.followers.addAll(friendList);

        }
        return friendList;
    }

//initliaze my configurations
    private void initConfigs() {
        consumerKey = getString(R.string.consumer_key);
        consumerSecret = getString(R.string.consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }



    //Save lists and info into shredPreference
    //save user list to handle the signed in already user and go to followers activity directly

    private void saveTwitterInfo(AccessToken accessToken) {

        long userId = accessToken.getUserId();

        User user;

        try {

            user = twitter.showUser(userId);
            String username = user.getName();

            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.putLong(PREF_USER_Id,userId);



            try {
                e.putString("UserList", ObjectSerializer.serialize((Serializable) getFriendList()));
            } catch (IOException es) {
                es.printStackTrace();
            }

            e.commit();

        } catch (twitter4j.TwitterException e) {
            e.printStackTrace();
        }
    }

    // build the config of twitter4j library
    //  if not looged thensend intent to the web view with auth url by getting callbak
    //if logged go to followers screen and skip looging
    private void loginToTwitter() {

        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        if(!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
            }
        } else {


//            getFriendList();

            Intent intent=new Intent(this,Followers.class);

            startActivity(intent);
        }
    }

    //when i get the activity result the logging succeds then i will take username and id and start the follower activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK) {
            String verifier = data.getExtras().getString(oAuthVerifier);

            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userId = accessToken.getUserId();
                final User user = twitter.showUser(userId);
                String username = user.getName();

                saveTwitterInfo(accessToken);


                //go to followers


                getFriendList();

                Intent intent=new Intent(this,Followers.class);
                intent.putExtra("Namee",username);
                intent.putExtra("twittersession_id",userId);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //when button clicks go to logintwitter

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                loginToTwitter();
                break;

        }
    }


}
