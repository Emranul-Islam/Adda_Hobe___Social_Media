package com.muhammad_sohag.socialmedia;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Setting extends AppCompatActivity {

    @BindView(R.id.setting_profile_image)
    protected ImageView profile_image;
    @BindView(R.id.setting_cover_image)
    protected ImageView cover_image;
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
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
                        .load(documentSnapshot.getString("photo"))
                        .into(profile_image);
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
}
