package com.muhammad_sohag.socialmedia.Setting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muhammad_sohag.socialmedia.R;

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
    @BindView(R.id.setting_bio)
    protected TextView bio;
    @BindView(R.id.setting_batch)
    protected TextView batch;
    @BindView(R.id.setting_department)
    protected TextView department;
    private String DATA_NAME, DATA_BATCH, DATA_DEPARTMENT,DATA_BIO,DATA_PROFILE,DATA_COVER;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference databaseRef = database.collection("USERS").document(auth.getUid());
    private StorageReference storeg = FirebaseStorage.getInstance().getReference();
    private StorageReference coverStoreg = storeg.child("Photo").child("Cover").child(auth.getUid()+".jpg");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        //Auto Profile Data show :
        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile Setting");
        loadData();

        names.setOnClickListener(v -> editNameData());
        bio.setOnClickListener(v -> editBioData());
        batch.setOnClickListener(v -> editBatchData());
        department.setOnClickListener(v -> editDepartmentData());

        coverImage.setOnClickListener(v -> {
            Intent coverIntent = new Intent(Setting.this,CoverChange.class);
            coverIntent.putExtra("cover",DATA_COVER);
            startActivity(coverIntent);
        });
        profileImage.setOnClickListener(v -> {
            Intent profileIntent = new Intent(Setting.this, ProfileChange.class);
            profileIntent.putExtra("profile",DATA_PROFILE);
            startActivity(profileIntent);
        });

    }

    //Load Data start:------------------------->
    @SuppressLint("CheckResult")
    private void loadData() {
        databaseRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                DATA_PROFILE = documentSnapshot.getString("profileUrl");
                DATA_COVER = documentSnapshot.getString("coverUrl");
                DATA_NAME = documentSnapshot.getString("name");
                DATA_BIO = documentSnapshot.getString("bio");
                DATA_BATCH = documentSnapshot.getString("batch");
                DATA_DEPARTMENT = documentSnapshot.getString("department");

                names.setText(DATA_NAME);
                if (DATA_BIO != null){
                    bio.setText(DATA_BIO);
                }
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
                        .load(DATA_PROFILE)
                        .into(profileImage);
                Glide.with(Setting.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(DATA_COVER)
                        .into(coverImage);


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
        newEditText.setMinEms(15);

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
                .addOnCompleteListener(Setting.this, task -> {
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
                .addOnCompleteListener(Setting.this, task -> {
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
                .addOnCompleteListener(Setting.this, task -> {
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

    //Bio   Method:----------------------->
    private void editBioData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setMessage("Edit Bio");

        LinearLayout linearLayout = new LinearLayout(this);

        EditText newEditText = new EditText(this);
        newEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        newEditText.setText(DATA_BIO);
        newEditText.setHint("Write Bio Max 110 char");
        newEditText.setMinEms(20);
        newEditText.setMaxEms(110);

        linearLayout.addView(newEditText);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Bio Changing...");
            progressDialog.show(); //Progress Dialog is created to loading
            String finalBio = newEditText.getText().toString().trim();
            //Name Change Method Call
            bioChange(finalBio, progressDialog);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
    //Bio Change Method:
    private void bioChange(String name, ProgressDialog progressDialog) {
        Map<String, Object> value = new HashMap<>();
        value.put("bio", name);

        databaseRef.update(value)
                .addOnCompleteListener(Setting.this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Success Bio Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Setting.this, "Error to change: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //-------------Bio  Method End---------------*>




}


