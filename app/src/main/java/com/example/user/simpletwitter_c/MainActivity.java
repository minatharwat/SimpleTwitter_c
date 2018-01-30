package com.example.user.simpletwitter_c;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.simpletwitter_c.Model.MyFollower;
import com.example.user.simpletwitter_c.Utilies.ObjectSerializer;
import com.google.gson.Gson;

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

import static com.example.user.simpletwitter_c.Followers.followers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";
    private static final String PREF_USER_Id = "user_id";
    private static final String Gson_Objects="gson_objects";


    public static final int WEBVIEW_REQUEST_CODE = 100;



    private static twitter4j.Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences sharedPreferences;

    Gson gson;

    List<MyFollower> myList;
    List<User> slist=null;

    private View loginLayout;
    List<User> caList;

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);

        findViewById(R.id.btn_login).setOnClickListener(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, 0);

        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        if(isLoggedIn) {


            String username = sharedPreferences.getString(PREF_USER_NAME, "");
            Long userId = sharedPreferences.getLong(PREF_USER_Id, 0);

            //go to followers
           // getFriendList();
           // followers.addAll(getFriendList());



        // String s =sharedPreferences.getString(Gson_Objects,"");


            //followers.addAll(caList);

            try {
                followers = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("UserList", ObjectSerializer.serialize(new ArrayList())));
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

                   // saveTwitterInfo(accessToken);

                    //loginLayout.setVisibility(View.GONE);

                   // followers.clear();
                   // followers.addAll(getFriendList());


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
            followers=new ArrayList<User>();
            followers.addAll(friendList);
/*
           slist=new ArrayList<>();
           slist.addAll(friendList);

            myList=new ArrayList<>();
            for (int i=0;i<slist.size();i++) {

                myList.get(i).setFullname(slist.get(i).getName());
                myList.get(i).setProfile_url_pic(slist.get(i).getOriginalProfileImageURL());
                myList.get(i).setBio(slist.get(i).getDescription());

            }
            */
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

            //gson = new Gson();
           // String jsonlist = gson.toJson(getFriendList());
            //e.putString(Gson_Objects,gson.toJson(getFriendList()));

/*
            myList=new ArrayList<>();
            for (int i=0;i<getFriendList().size();i++) {

                myList.get(i).setFullname(getFriendList().get(i).getName());
                myList.get(i).setProfile_url_pic(getFriendList().get(i).getOriginalProfileImageURL());
                myList.get(i).setBio(getFriendList().get(i).getDescription());
            }*/
            e.putString(Gson_Objects, String.valueOf(myList));

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
           // loginLayout.setVisibility(View.GONE);
            //go to followers
           // shareLayout.setVisibility(View.VISIBLE);

//            getFriendList();

            Intent intent=new Intent(this,Followers.class);

            startActivity(intent);
        }
    }

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

                //loginLayout.setVisibility(View.GONE);
                //go to followers
               // shareLayout.setVisibility(View.VISIBLE);

              //  userName.setText(MainActivity.this.getResources().getString(R.string.hello)
                //        + " " +username);


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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                loginToTwitter();
                break;

        }
    }


}
