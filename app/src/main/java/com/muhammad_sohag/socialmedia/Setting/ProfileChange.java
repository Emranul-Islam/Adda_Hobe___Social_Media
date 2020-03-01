package com.muhammad_sohag.socialmedia.Setting;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.muhammad_sohag.socialmedia.R;

public class ProfileChange extends AppCompatActivity {
    private ImageView profilePhoto;
    private EditText profileCaption;
    private Button profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        profilePhoto = findViewById(R.id.pc_photo);
        profileCaption = findViewById(R.id.pc_post);
        profileBtn = findViewById(R.id.pc_btn);

        String profileLink = getIntent().getStringExtra("profile");

    }
}
