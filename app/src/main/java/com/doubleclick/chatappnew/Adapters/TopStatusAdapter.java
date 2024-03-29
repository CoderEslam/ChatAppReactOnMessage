package com.doubleclick.chatappnew.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doubleclick.chatappnew.Activities.MainActivity;
import com.doubleclick.chatappnew.Models.Status;
import com.doubleclick.chatappnew.Models.UserStatus;
import com.doubleclick.chatappnew.R;
import com.doubleclick.chatappnew.databinding.ItemStatusBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);
//        Toast.makeText(holder.itemView.getContext(),"Size or Statuse  = "+getItemCount(),Toast.LENGTH_LONG).show();
//        Toast.makeText(holder.itemView.getContext(),"Name   = "+userStatus.getName(),Toast.LENGTH_LONG).show();
//        Toast.makeText(holder.itemView.getContext(),"Image  = "+userStatus.getProfileImage(),Toast.LENGTH_LONG).show();
//        Toast.makeText(holder.itemView.getContext(),"Last update  = "+userStatus.getLastUpdated(),Toast.LENGTH_LONG).show();


        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() -1);

//        Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.image);
        Picasso.get().load(lastStatus.getImageUrl()).placeholder(R.drawable.avatar).into(holder.binding.image);
        Log.v("URL image  = ",""+lastStatus.getImageUrl());
//        Glide.with(context).load(userStatus.getProfileImage()).into(holder.binding.image);


        Toast.makeText(holder.itemView.getContext()," ImageUrl = "+lastStatus.getImageUrl(),Toast.LENGTH_LONG).show();


        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }


                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
