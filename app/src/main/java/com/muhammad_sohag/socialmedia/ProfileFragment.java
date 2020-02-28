package com.muhammad_sohag.socialmedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


}
