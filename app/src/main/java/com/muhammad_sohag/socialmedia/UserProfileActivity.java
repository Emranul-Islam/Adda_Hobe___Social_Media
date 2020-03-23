package com.muhammad_sohag.socialmedia;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.muhammad_sohag.socialmedia.adapter.PostAdapter;
import com.muhammad_sohag.socialmedia.model.PostModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    @BindView(R.id.user_cover_image)
    protected ImageView coverImage;
    @BindView(R.id.user_profile_image)
    protected ImageView profileImage;
    @BindView(R.id.user_profile_name)
    protected TextView name;
    @BindView(R.id.user_profile_bio)
    protected TextView bio;
    @BindView(R.id.user_profile_batch)
    protected TextView batch;
    @BindView(R.id.user_profile_department)
    protected TextView department;
    @BindView(R.id.user_profile_toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.user_profile_recycler)
    protected RecyclerView userPostRecycler;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference postRef = database.collection("POST");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);


        //Getting data from adapter:

        String userIdData = getIntent().getStringExtra("userId");


        setSupportActionBar(toolbar);


        //realtime user info retrive hobe firebase theke:
        realTimeProfileInfo(userIdData);
        //post gola dekar method call kora hoiche:
        postLoad(userIdData);


    }

    private void realTimeProfileInfo(String dataUserId) {
        database.collection("USERS").document(dataUserId)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null){
                            String profileLink = documentSnapshot.getString("profileUrl");
                            String coverLink = documentSnapshot.getString("coverUrl");
                            String nameData = documentSnapshot.getString("name");
                            String bioData = documentSnapshot.getString("bio");
                            String batchData = documentSnapshot.getString("batch");
                            String departmentData = documentSnapshot.getString("department");

                            getSupportActionBar().setTitle(nameData);
                            name.setText(nameData);

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.ic_launcher_background);
                            Glide.with(UserProfileActivity.this)
                                    .setDefaultRequestOptions(requestOptions)
                                    .load(profileLink)
                                    .into(profileImage);

                            RequestOptions ro = new RequestOptions();
                            ro.placeholder(R.drawable.ic_launcher_background);
                            Glide.with(UserProfileActivity.this)
                                    .setDefaultRequestOptions(ro)
                                    .load(coverLink)
                                    .into(coverImage);
                            if (bioData != null) {
                                bio.setText(bioData);
                            }
                            if (batchData != null) {
                                batch.setText(String.format("Batch Name: %s", batchData));
                            }
                            if (departmentData != null) {
                                department.setText(String.format("Department Name: %s", departmentData));
                            }
                        }
                    }
                });
    }

    //show user post
    private void postLoad(String userId) {
        userPostRecycler.setLayoutManager(new LinearLayoutManager(this));
        userPostRecycler.setHasFixedSize(true);
        List<PostModel> postModels = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(UserProfileActivity.this, postModels);
        userPostRecycler.setAdapter(adapter);
        Query query = postRef.whereEqualTo("userId", userId);
        query.orderBy("postId", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    PostModel postModel = documentChange.getDocument().toObject(PostModel.class);
                                    postModels.add(postModel);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            Log.d(TAG, "onEvent: " + e);
                        }
                    }
                });
    }
}
