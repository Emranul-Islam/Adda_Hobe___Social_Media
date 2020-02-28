package com.muhammad_sohag.socialmedia;

import android.content.Intent;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView name,batchName,departmentName;
    private ImageView profileImage, coverImage;
    private Button addPost,editProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profile_name);
        batchName = view.findViewById(R.id.profile_batch);
        departmentName = view.findViewById(R.id.profile_department);
        profileImage = view.findViewById(R.id.profile_profile_image);
        coverImage = view.findViewById(R.id.profile_cover_image);
        addPost = view.findViewById(R.id.profile_add_postBTN);
        editProfile = view.findViewById(R.id.profile_editBTN);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //add post button clicked
        addPost.setOnClickListener(v -> Toast.makeText(getActivity(), "Add Post Button Clicked", Toast.LENGTH_SHORT).show());
        //edit button clicked
        editProfile.setOnClickListener(v -> {
            Intent pIntent = new Intent(getActivity(),Setting.class);
            startActivity(pIntent);
        });


    }
}
