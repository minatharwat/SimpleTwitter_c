package com.example.user.simpletwitter_c.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.example.user.simpletwitter_c.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

public class FollowerInfo extends AppCompatActivity {

    String img_url_prof;
    String back_img;
    String username;
    long id;
    RecyclerView recyclerView;

    ImageView prof;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_info);

        back = findViewById(R.id.image_back);
        prof = findViewById(R.id.profile_i);


        recyclerView = findViewById(R.id.recy_tweets);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //configurations of twitter

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);


        //get image and background and user id

        img_url_prof = getIntent().getStringExtra("prof_image");
        back_img = getIntent().getStringExtra("background_image");
        username = getIntent().getStringExtra("name");
        id = getIntent().getLongExtra("id", 0);


        //get tweets of the user and retweets not included

        UserTimeline userTimeline = new UserTimeline.Builder().userId(id).maxItemsPerRequest(10).includeRetweets(false).build();


        final TweetTimelineRecyclerViewAdapter adapter =
                new TweetTimelineRecyclerViewAdapter.Builder(this)
                        .setTimeline(userTimeline)

                        .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                        .build();

        recyclerView.setAdapter(adapter);


        //load pofile and image photos


        Picasso.with(getBaseContext()).load(img_url_prof).error(R.drawable.images).into(prof);

        if (back_img != null) {
            Picasso.with(getBaseContext()).load(back_img).error(R.drawable.images).into(back);
        }


    }


}
