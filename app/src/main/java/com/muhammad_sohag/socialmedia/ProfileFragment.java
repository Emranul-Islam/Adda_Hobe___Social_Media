package com.muhammad_sohag.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muhammad_sohag.socialmedia.Setting.Setting;
import com.muhammad_sohag.socialmedia.adapter.PostAdapter;
import com.muhammad_sohag.socialmedia.model.PostModel;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView name, bio, batchName, departmentName;
    private ImageView profileImage, coverImage;
    private Button addPost, editProfile;
    private RecyclerView postRecycler;
    private static final String TAG ="ProfileFragment";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference databaseRef = database.collection("USERS").document(auth.getUid());
    private CollectionReference postRef = database.collection("POST");

    private String DATA_PROFILE, DATA_COVER, DATA_NAME, DATA_BIO, DATA_BATCH, DATA_DEPARTMENT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profile_name);
        bio = view.findViewById(R.id.profile_bio);
        batchName = view.findViewById(R.id.profile_batch);
        departmentName = view.findViewById(R.id.profile_department);
        profileImage = view.findViewById(R.id.profile_profile_image);
        coverImage = view.findViewById(R.id.profile_cover_image);
        addPost = view.findViewById(R.id.profile_add_postBTN);
        editProfile = view.findViewById(R.id.profile_editBTN);
        postRecycler = view.findViewById(R.id.profile_recycler);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Load All Data:
        loadData();
        //add post button clicked
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddPostActivity.class);
                startActivity(intent);
            }
        });
        //edit button clicked
        editProfile.setOnClickListener(v -> {
            Intent eIntent = new Intent(getActivity(), Setting.class);
            startActivity(eIntent);
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadProfileImage();
            }
        });

        //postLoad:
        postLoad();

    }

    private void postLoad() {
        postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecycler.setHasFixedSize(true);
        List<PostModel> postModels = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(getActivity(),postModels);
        postRecycler.setAdapter(adapter);

        Query query = postRef.whereEqualTo("userId",auth.getUid()).orderBy("postId",Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType()== DocumentChange.Type.ADDED){
                            PostModel postModel = documentChange.getDocument().toObject(PostModel.class);
                            postModels.add(postModel);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }else {
                   Log.d(TAG, "onEvent: "+e);
                }
            }
        });
    }

    //Load Data start:------------------------->

    private void loadData() {

        databaseRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                DATA_PROFILE = documentSnapshot.getString("profileUrl");
                DATA_COVER = documentSnapshot.getString("coverUrl");
                DATA_NAME = documentSnapshot.getString("name");
                DATA_BIO = documentSnapshot.getString("bio");
                DATA_BATCH = documentSnapshot.getString("batch");
                DATA_DEPARTMENT = documentSnapshot.getString("department");
                name.setText(DATA_NAME);
                if (DATA_BIO != null){
                    bio.setText(DATA_BIO);
                }
                if (DATA_BATCH != null) {
                    batchName.setText(String.format("Batch Name: %s", DATA_BATCH));
                }
                if (DATA_DEPARTMENT != null) {
                    departmentName.setText(String.format("Department Name: %s", DATA_DEPARTMENT));
                }

                //Image Load
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_launcher_background);
                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(DATA_PROFILE)
                        .into(profileImage);

                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(DATA_COVER)
                        .into(coverImage);
            } else {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
