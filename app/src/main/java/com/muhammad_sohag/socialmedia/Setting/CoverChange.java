package com.muhammad_sohag.socialmedia.Setting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muhammad_sohag.socialmedia.MainActivity;
import com.muhammad_sohag.socialmedia.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class CoverChange extends AppCompatActivity {
    //Net Connection verify:
    private ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    private NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    private NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

    private ImageView coverImage;
   // private EditText coverCaption;
    private Button coverBtn;
    private Uri coverImageURI;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = auth.getUid();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference databaseRef = database.collection("USERS").document(userId);
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private StorageReference coverRef = storage.child("Photo").child("Cover").child(userId + ".jpg");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_change);

        coverBtn = findViewById(R.id.cc_btn);
      //  coverCaption = findViewById(R.id.cc_caption);
        coverImage = findViewById(R.id.cc_photo);

        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCoverImage();
            }
        });

        coverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coverImageURI != null){
                    uploadPhoto();
                }else {
                    Toast.makeText(CoverChange.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //------------Adding crop system------------

    //Upload Profile Image
    private void selectCoverImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(CoverChange.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CoverChange.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                cropCoverImage();

            } else {
                cropCoverImage();
            }

        } else {
            cropCoverImage();
        }
    }

    //Crop image method:----------->
    private void cropCoverImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(2, 1)
                .start(CoverChange.this);
    }

    //On activity result:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    coverImageURI = result.getUri();
                    coverImage.setImageURI(coverImageURI);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Upload photo to the Online
    private void uploadPhoto() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cover Image Uploading...");
        progressDialog.show(); //Progress Dialog is created to loading

        coverRef.putFile(coverImageURI)
                .addOnCompleteListener(CoverChange.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            coverRef.getDownloadUrl().addOnCompleteListener(CoverChange.this, new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String coverImageDownloadUrl = task.getResult().toString();
                                        Map<String, Object> link = new HashMap<>();
                                        link.put("coverUrl", coverImageDownloadUrl);
                                        databaseRef.update(link)
                                                .addOnCompleteListener(CoverChange.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(CoverChange.this, "Cover Change Success", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(CoverChange.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(CoverChange.this, "Error To update", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(CoverChange.this, "Error To getting link", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(CoverChange.this, "Error To Upload", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CoverChange.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });

    }



}
