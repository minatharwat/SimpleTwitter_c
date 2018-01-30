package com.example.user.simpletwitter_c;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import twitter4j.User;

public class Followers extends AppCompatActivity {
    String flag;
    long id_o;

    TextView textView;

  public static List<User> followers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);



         textView=(TextView)findViewById(R.id.theusername) ;

        //get username and id after logging
        flag = getIntent().getStringExtra("Namee");
        id_o = getIntent().getLongExtra("twittersession_id", 0);

      //  Intent i = getIntent();
      //  followers = (List<User>) i.getSerializableExtra("LIST");

        Toast.makeText(this, "ccccccccccccccccccccccccc"+followers, Toast.LENGTH_SHORT).show();
        textView.setText(followers.get(0).getOriginalProfileImageURL()+"    "+followers.get(0).getName()+
                followers.get(1).getOriginalProfileImageURL()+"    "+followers.get(1).getName());


    }

}