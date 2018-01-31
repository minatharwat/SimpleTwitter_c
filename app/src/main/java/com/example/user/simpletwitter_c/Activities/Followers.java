package com.example.user.simpletwitter_c.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.user.simpletwitter_c.Adapters.FollowersRecyc;
import com.example.user.simpletwitter_c.R;

import java.util.List;

import twitter4j.User;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class Followers extends AppCompatActivity {
    String flag;
    long id_o;


    public RecyclerView recyclerView;
    public FollowersRecyc adapter;

  public static List<User> followers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);



        //get username and id after logging
        flag = getIntent().getStringExtra("Namee");
        id_o = getIntent().getLongExtra("twittersession_id", 0);


        //  Toast.makeText(this, "ccccccccccccccccccccccccc"+followers, Toast.LENGTH_SHORT).show();


        // set controls

        recyclerView = findViewById(R.id.followers_recyc);


        ;
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        }
        adapter = new FollowersRecyc(followers, this);

        recyclerView.setAdapter(adapter);




    }


}