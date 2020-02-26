package com.muhammad_sohag.socialmedia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Setting extends AppCompatActivity {

    @BindView(R.id.setting_profile_image)
    protected ImageView profileImage;
    @BindView(R.id.setting_cover_image)
    protected ImageView coverImage;
    @BindView(R.id.setting_name)
    protected TextView names;
    @BindView(R.id.setting_batch)
    protected TextView batch;
    @BindView(R.id.setting_department)
    protected TextView department;
    @BindView(R.id.s_name_edit)
    protected ImageView nameEdit;
    @BindView(R.id.s_batch_edit)
    protected ImageView batchEdit;
    @BindView(R.id.s_department_edit)
    protected ImageView departmentEdit;
    private String DATA_NAME, DATA_BATCH, DATA_DEPARTMENT;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference databaseRef = database.collection("USERS").document(auth.getUid());
    private StorageReference storeg = FirebaseStorage.getInstance().getReference();
    private StorageReference profileStoreg = storeg.child("Profile").child(auth.getUid()+".jpg");

    private Uri profileImageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        //Auto Profile Data show :
        loadData();

        nameEdit.setOnClickListener(v -> editNameData());
        batchEdit.setOnClickListener(v -> editBatchData());
        departmentEdit.setOnClickListener(v -> editDepartmentData());
        profileImage.setOnClickListener(v -> selectProfileImage());

    }

    //Load Data start:------------------------->
    @SuppressLint("CheckResult")
    private void loadData() {
        databaseRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                DATA_NAME = documentSnapshot.getString("name");
                DATA_BATCH = documentSnapshot.getString("batch");
                DATA_DEPARTMENT = documentSnapshot.getString("department");
                names.setText(DATA_NAME);
                if (DATA_BATCH != null) {
                    batch.setText(String.format("Batch Name: %s", DATA_BATCH));
                }
                if (DATA_DEPARTMENT != null) {
                    department.setText(String.format("Department Name: %s", DATA_DEPARTMENT));
                }

                //Image Load
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_launcher_background);
                Glide.with(Setting.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(documentSnapshot.getString("profileUrl"))
                        .into(profileImage);
            } else {
                Toast.makeText(Setting.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //----------Load Data End------------------*>

    //Edit Name  Method:----------------------->
    private void editNameData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setMessage("Edit Name");

        LinearLayout linearLayout = new LinearLayout(this);

        EditText newEditText = new EditText(this);
        newEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        newEditText.setText(DATA_NAME);
        newEditText.setMinEms(10);

        linearLayout.addView(newEditText);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Name Changing...");
            progressDialog.show(); //Progress Dialog is created to loading
            String finalName = newEditText.getText().toString().trim();
            //Name Change Method Call
            nameChange(finalName, progressDialog);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();


    }
    //Name Change Method:
    private void nameChange(String name, ProgressDialog progressDialog) {
        Map<String, Object> value = new HashMap<>();
        value.put("name", name);

        databaseRef.update(value)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Success Name Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Setting.this, "Error to change: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //-------------Edit Name  Method End---------------*>

    //Batch  Change Method:--------------------->
    private void editBatchData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setMessage("Edit Batch");

        LinearLayout linearLayout = new LinearLayout(this);

        EditText newEditText = new EditText(this);
        newEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        if (DATA_BATCH != null) {
            newEditText.setText(DATA_BATCH);
        }
        newEditText.setMinEms(15);

        linearLayout.addView(newEditText);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Batch Name Changing...");
            progressDialog.show(); //Progress Dialog is created to loading
            String finalName = newEditText.getText().toString().trim();
            //Name Change Method Call
            batchChange(finalName, progressDialog);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();


    }
    //Batch Change Method:
    private void batchChange(String name, ProgressDialog progressDialog) {
        Map<String, Object> value = new HashMap<>();
        value.put("batch", name);

        databaseRef.update(value)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Success Batch Name Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Setting.this, "Error to change: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //--------------Batch  Change Method End--------------*>

    //Department Name  Method:----------------->
    @SuppressLint("ResourceAsColor")
    private void editDepartmentData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setMessage("Edit Department");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(Color.rgb(250,250,250));
        EditText newEditText = new EditText(this);
        newEditText.setTextColor(R.color.colorPrimaryDark);
        newEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        if (DATA_DEPARTMENT != null) {
            newEditText.setText(DATA_DEPARTMENT);
        }
        newEditText.setMinEms(15);

        linearLayout.addView(newEditText);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Department Name Changing...");
            progressDialog.show(); //Progress Dialog is created to loading
            String finalName = newEditText.getText().toString().trim();
            //Name Change Method Call
            departmentChange(finalName, progressDialog);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();


    }
    //Department Change Method:
    private void departmentChange(String name, ProgressDialog progressDialog) {
        Map<String, Object> value = new HashMap<>();
        value.put("department", name);

        databaseRef.update(value)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Success Department Name Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Setting.this, "Error to change: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //--------------Department Name  Method End-----------------*>


    //------------Adding crop system------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageURI = result.getUri();

                uploadPhoto();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Crop image method:----------->
    private void cropProfileImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(Setting.this);

    }

    //Upload Profile Image
    private void selectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(Setting.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Setting.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                cropProfileImage();

            } else {
                cropProfileImage();
            }

        } else {
            cropProfileImage();
        }
    }

    //Upload photo to the Online
    private void uploadPhoto() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Profile Image Uploading...");
        progressDialog.show(); //Progress Dialog is created to loading

        if (profileImageURI != null){
            profileStoreg.putFile(profileImageURI)
                    .addOnCompleteListener(Setting.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                profileStoreg.getDownloadUrl().addOnCompleteListener(Setting.this,new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String profileImageDownloadUrl = task.getResult().toString();
                                        if (profileImageDownloadUrl != null){
                                            Map<String, Object> link = new HashMap<>();
                                            link.put("profileUrl",profileImageDownloadUrl);
                                            databaseRef.update(link)
                                                    .addOnCompleteListener(Setting.this, new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                profileImage.setImageURI(profileImageURI);
                                                                progressDialog.dismiss();
                                                            }else{
                                                                progressDialog.dismiss();
                                                                Toast.makeText(Setting.this, "Error To update", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(Setting.this, "Error To getting link", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(Setting.this, "Error To Upload", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}


