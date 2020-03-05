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
        String dataName = getIntent().getStringExtra("name");
        String dataBio = getIntent().getStringExtra("bio");
        String dataBatch = getIntent().getStringExtra("batch");
        String dataDepartment = getIntent().getStringExtra("department");
        String dataProfile = getIntent().getStringExtra("profile");
        String dataCover = getIntent().getStringExtra("cover");
        String dataUserId = getIntent().getStringExtra("userId");

        Toast.makeText(this, "user "+dataUserId, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "name "+dataName, Toast.LENGTH_SHORT).show();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(dataName);
        //post gola dekar method call kora hoiche:
        postLoad(dataUserId);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(dataProfile)
                .into(profileImage);
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(dataCover)
                .into(coverImage);
        name.setText(dataName);
        if (dataBio != null) {
            bio.setText(dataBio);
        }
        if (dataBatch != null) {
            batch.setText(String.format("Batch Name: %s", dataBatch));
        }
        if (dataDepartment != null) {
            department.setText(String.format("Department Name: %s", dataDepartment));
        }


    }

    //show user post
    private void postLoad(String userId) {
        userPostRecycler.setLayoutManager(new LinearLayoutManager(this));
        userPostRecycler.setHasFixedSize(true);
        List<PostModel> postModels = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(UserProfileActivity.this, postModels);
        userPostRecycler.setAdapter(adapter);
        Toast.makeText(this, "1 --"+userId, Toast.LENGTH_SHORT).show();
        Query query = postRef.whereEqualTo("userId", userId);
        query.orderBy("postId", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            Toast.makeText(UserProfileActivity.this, "2", Toast.LENGTH_SHORT).show();
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    Toast.makeText(UserProfileActivity.this, "3", Toast.LENGTH_SHORT).show();
                                    PostModel postModel = documentChange.getDocument().toObject(PostModel.class);
                                    postModels.add(postModel);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(UserProfileActivity.this, "4", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            Log.d(TAG, "onEvent: " + e);
                        }
                    }
                });
    }
}
