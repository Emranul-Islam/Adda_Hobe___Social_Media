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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.muhammad_sohag.socialmedia.R;
import com.muhammad_sohag.socialmedia.model.PostModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<PostModel> postModelList;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String myUid = auth.getUid();
    private static final String TAG = "PostAdapter";

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


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        //Format Date:
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(postModelList.get(position).getTimestamp()));
        String timestamp = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();

        //gating image uri and user id
        final String userId = postModelList.get(position).getUserId();
        final String postId = postModelList.get(position).getPostId();
        String postImageLink = postModelList.get(position).getPostImageLink();

        holder.postTimestamp.setText(timestamp);
        holder.postCaption.setText(postModelList.get(position).getPostCaption());

        //Realtime profile name and image set
        profileSetup(holder.profileName, holder.profileImage, userId);

        //set post image
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        if (postImageLink.equals("noImage")) {
            holder.postImage.setVisibility(View.GONE);
            holder.postCaption.setTextSize(15);
            holder.postCaption.setPadding(5, 15, 5, 15);
            holder.postCaption.setTextColor(Color.BLACK);
            if (holder.postCaption.length() < 200) {
                holder.postCaption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                holder.postCaption.setTextSize(20);
                holder.postCaption.setPadding(50, 50, 50, 50);
                // holder.postCaption.setBackgroundColor(Color.GRAY);
            }
        } else {
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(postImageLink)
                    .into(holder.postImage);
        }

        //Like System:
        //firebase reference
        DocumentReference likeRef = database.collection("POST").document(postId)
                .collection("likes").document(myUid);
        //add like
        holder.likePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            if (!task.getResult().exists()) {
                                Toast.makeText(context, "Like Added", Toast.LENGTH_SHORT).show();
                                Map<String, Object> lValue = new HashMap<>();
                                lValue.put("likeId", myUid);
                                lValue.put("timestamp", FieldValue.serverTimestamp());
                                likeRef.set(lValue);
                            } else {
                                Toast.makeText(context, "Like Delete", Toast.LENGTH_SHORT).show();
                                likeRef.delete();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Please check internet connection..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        //like check:
        likeRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    holder.likePost.setImageDrawable(context.getDrawable(R.drawable.like_sel_ic));

                }else {
                    holder.likePost.setImageDrawable(context.getDrawable(R.drawable.like_ic));

                }
            }
        });
        //like count:
        database.collection("POST").document(postId).collection("likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            int count = queryDocumentSnapshots.size();
                            holder.likeCount(count);
                        }else {
                            holder.likeCount(0);
                        }
                    }
                });

    }

    //Post karir realtime data ene deyar method:
    private void profileSetup(TextView profileName, ImageView profileImage, String userId) {
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
        TextView profileName, postTimestamp, postCaption, likeCount;
        ImageView profileImage, postImage, likePost;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.item_post_profile_image);
            postImage = itemView.findViewById(R.id.item_post_image);
            profileName = itemView.findViewById(R.id.item_post_profile_name);
            postTimestamp = itemView.findViewById(R.id.item_post_time);
            postCaption = itemView.findViewById(R.id.item_post_caption);
            likePost = itemView.findViewById(R.id.item_post_like);
        }

        void likeCount(int count){
            likeCount = itemView.findViewById(R.id.item_post_like_count);
            likeCount.setText(count +" Likes");
        }
    }
}
