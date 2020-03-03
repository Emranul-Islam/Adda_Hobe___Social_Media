package com.muhammad_sohag.socialmedia.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muhammad_sohag.socialmedia.R;
import com.muhammad_sohag.socialmedia.model.PostModel;

import java.util.Calendar;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<PostModel> postModelList;

    public PostAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_show_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Format Date:
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(postModelList.get(position).getTimestamp()));
        String timestamp = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();
        //gating image uri and user id
        final String userId = postModelList.get(position).getUserId();
        String postImageLink = postModelList.get(position).getPostImageLink();

        holder.postTimestamp.setText(timestamp);
        holder.postCaption.setText(postModelList.get(position).getPostCaption());

        //Realtime profile name and image set
        profileSetup(holder.profileName, holder.profileImage, userId);

        //set post image
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        if (postImageLink != null) {
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(postImageLink)
                    .into(holder.postImage);

        } else {
            holder.postImage.setVisibility(View.GONE);
            holder.postCaption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.postCaption.setTextSize(15);
            holder.postCaption.setPadding(5, 15, 5, 15);
            holder.postCaption.setTextColor(Color.WHITE);
            holder.postCaption.setBackgroundColor(Color.GRAY);
            if (holder.postCaption.length() < 20) {
                holder.postCaption.setTextSize(20);
                holder.postCaption.setPadding(5, 50, 5, 50);
                holder.postCaption.setBackgroundColor(Color.GRAY);
            }
        }

    }

    //Post karir realtime data ene deyar method:
    private void profileSetup(TextView profileName, ImageView profileImage, String userId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference databaseRef = database.collection("USERS").document(userId);
        databaseRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                profileName.setText(documentSnapshot.getString("name"));
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_launcher_background);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(documentSnapshot.getString("profileUrl"))
                        .into(profileImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView profileName, postTimestamp, postCaption;
        ImageView profileImage, postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.item_post_profile_image);
            postImage = itemView.findViewById(R.id.item_post_image);
            profileName = itemView.findViewById(R.id.item_post_profile_name);
            postTimestamp = itemView.findViewById(R.id.item_post_time);
            postCaption = itemView.findViewById(R.id.item_post_caption);
        }
    }
}
