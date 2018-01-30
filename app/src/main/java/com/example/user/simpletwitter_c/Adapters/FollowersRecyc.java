package com.example.user.simpletwitter_c.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.simpletwitter_c.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import twitter4j.User;

/**
 * Created by ${Mina} on 30/01/2018.
 */

public class FollowersRecyc extends RecyclerView.Adapter<FollowersRecyc.viewHolder> {
    List<User> m;
    private Context mcontext;
    public User obj;
    public static int pos;
    public static int x;

    public FollowersRecyc(List<User> m, Context mcontext) {
        this.m = m;
        this.mcontext = mcontext;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        viewHolder viewHolder = new viewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        obj = m.get(position);
        holder.name.setText(obj.getName());


        Picasso.with(mcontext).load(obj.getOriginalProfileImageURL()).error(R.drawable.twitter).into(holder.imageView);
        if (obj.getDescription() != null) {
            holder.bio.setText(obj.getDescription());
        } else {
            holder.bio.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return m.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView imageView;
        TextView bio;

        public viewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.full_name);
            imageView = (ImageView) itemView.findViewById(R.id.image_prof);
            bio = (TextView) itemView.findViewById(R.id.dec_bio);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


        }
    }


}

