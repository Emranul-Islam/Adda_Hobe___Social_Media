package com.muhammad_sohag.socialmedia.Setting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.muhammad_sohag.socialmedia.Custom.LoadingDialog;
import com.muhammad_sohag.socialmedia.MainActivity;
import com.muhammad_sohag.socialmedia.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class ProfileChange extends AppCompatActivity {
    private ImageView profilePhoto;
   // private EditText profileCaption;
    private Button profileBtn;
    private Uri profileImageURI = null;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = auth.getUid();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference databaseRef = database.collection("USERS").document(userId);
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private StorageReference profileRef = storage.child("Photo").child("Profile").child(userId + ".jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        profilePhoto = findViewById(R.id.pc_photo);
       // profileCaption = findViewById(R.id.pc_post);
        profileBtn = findViewById(R.id.pc_btn);


        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCoverImage();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileImageURI!=null){
                    uploadPhoto();
                }else{
                    Toast.makeText(ProfileChange.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //------------Adding crop system------------

    //Upload Profile Image
    private void selectCoverImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ProfileChange.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileChange.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                cropProfileImage();

            } else {
                cropProfileImage();
            }

        } else {
            cropProfileImage();
        }
    }

    //Crop image method:----------->
    private void cropProfileImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProfileChange.this);
    }

    //On activity result:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageURI = result.getUri();
                profilePhoto.setImageURI(profileImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Upload photo to the Online
    private void uploadPhoto() {
        //Progress Dialog is created to loading
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        profileRef.putFile(profileImageURI)
                .addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            profileRef.getDownloadUrl().addOnCompleteListener(ProfileChange.this, new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String profileImageDownloadUrl = task.getResult().toString();
                                        Map<String, Object> link = new HashMap<>();
                                        link.put("profileUrl", profileImageDownloadUrl);
                                        databaseRef.update(link)
                                                .addOnCompleteListener( new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            loadingDialog.dismissLoadingDialog();
                                                            Toast.makeText(ProfileChange.this, "Profile Change Success", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ProfileChange.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            loadingDialog.dismissLoadingDialog();
                                                            Toast.makeText(ProfileChange.this, "Error To update", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                        } else {
                            loadingDialog.dismissLoadingDialog();
                            Toast.makeText(ProfileChange.this, "Error To Upload", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(ProfileChange.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}
