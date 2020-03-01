package com.muhammad_sohag.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity {
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(dataName);


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
        if (dataBio != null){
            bio.setText(dataBio);
        }
        if (dataBatch != null){
            batch.setText(String.format("Batch Name: %s", dataBatch));
        }
        if (dataDepartment != null){
            department.setText(String.format("Department Name: %s", dataDepartment));
        }




    }
}
