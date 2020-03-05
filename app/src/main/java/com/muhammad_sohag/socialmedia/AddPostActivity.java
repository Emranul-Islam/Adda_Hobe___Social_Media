package com.muhammad_sohag.socialmedia;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageView postImage, postImageClear;
    private EditText postCaption;
    private TextView netConnection;
    private Button postBtn, addPhoto;
    private RelativeLayout postImageLayout;
    private Uri imageUri = null;

    private final String userId = FirebaseAuth.getInstance().getUid();
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore data = FirebaseFirestore.getInstance();
    private CollectionReference dataRef = data.collection("POST");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        postImage = findViewById(R.id.post_photo);
        postImageClear = findViewById(R.id.post_clear);
        postCaption = findViewById(R.id.post_caption);
        postBtn = findViewById(R.id.post_btn);
        addPhoto = findViewById(R.id.post_add_photo);
        postImageLayout = findViewById(R.id.post_image_layout);
        netConnection = findViewById(R.id.post_net_connection);

        //Net Connection verify:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobileNetwork.isConnected()){
            netConnection.setVisibility(View.GONE);
        }else {
            netConnection.setVisibility(View.VISIBLE);
        }

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = postCaption.getText().toString();

                if (wifi.isConnected() || mobileNetwork.isConnected()) {
                    if (!TextUtils.isEmpty(caption) || imageUri != null) {
                        if (imageUri != null) {
                            posting(caption, String.valueOf(imageUri));
                        } else {
                            posting(caption, "noImage");
                        }
                    } else {
                        Toast.makeText(AddPostActivity.this, "Select Photo or Write Post", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPostActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        postImageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                postImageLayout.setVisibility(View.GONE);
            }
        });
        //getting current time date

        //Convert Time Date from milliseconds
      /*  time = String.valueOf(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(time));
        String dateFormate = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();*/


    }

    private void posting(String caption, String imageUri) {
        ProgressDialog progressDialog = new ProgressDialog(AddPostActivity.this);
        progressDialog.setMessage("Posting..");
        progressDialog.show();
        String timestamp = String.valueOf(System.currentTimeMillis());
        if (!imageUri.equals("noImage")) {
            //Image soho kare Upload Hobe
            //Getting Storage Reference:
            StorageReference storageRef = storage.child("Photo").child("Posts").child("posts_" + timestamp + ".jpg");
            storageRef.putFile(Uri.parse(imageUri))
                    .addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageRef.getDownloadUrl().addOnCompleteListener(AddPostActivity.this, new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = String.valueOf(task.getResult());

                                            uploadPost(caption, downloadUrl, timestamp, progressDialog, storageRef);
                                        } else {
                                            storageRef.delete();
                                            progressDialog.dismiss();
                                            Toast.makeText(AddPostActivity.this, "Something Wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AddPostActivity.this, "Something Is Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            //Image charai upload hobe
            uploadPost(caption, "noImage", timestamp, progressDialog, null);
        }
    }

    private void uploadPost(String caption, String downloadUrl, String timestamp, ProgressDialog progressDialog, StorageReference storageRef) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("postId", timestamp);
        data.put("postCaption", caption);
        data.put("postImageLink", downloadUrl);
        data.put("timestamp", timestamp);
        dataRef.document(timestamp)
                .set(data)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Post Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (downloadUrl != null) {
                                storageRef.delete();
                            }
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    //Upload Profile Image
    private void selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                cropImage();

            } else {
                cropImage();
            }

        } else {
            cropImage();
        }
    }

    //Crop image method:----------->
    private void cropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .start(AddPostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                postImageLayout.setVisibility(View.VISIBLE);
                Toast.makeText(this, imageUri + "", Toast.LENGTH_SHORT).show();
                postImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
