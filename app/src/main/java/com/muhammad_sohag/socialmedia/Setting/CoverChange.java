package com.muhammad_sohag.socialmedia.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.muhammad_sohag.socialmedia.R;

public class CoverChange extends AppCompatActivity {
    private ImageView coverImage;
    private EditText coverCaption;
    private Button coverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_change);

        coverBtn = findViewById(R.id.cc_btn);
        coverCaption = findViewById(R.id.cc_caption);
        coverImage = findViewById(R.id.cc_photo);
    }
}
