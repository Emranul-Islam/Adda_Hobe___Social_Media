package com.muhammad_sohag.socialmedia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddPostActivity extends AppCompatActivity {

    private ImageView postImage;
    private EditText postCaption;
    private Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        postImage = findViewById(R.id.post_photo);
        postCaption = findViewById(R.id.post_caption);
        postBtn = findViewById(R.id.post_btn);
    }
}
