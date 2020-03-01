package com.muhammad_sohag.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muhammad_sohag.socialmedia.Setting.Setting;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView name, bio, batchName, departmentName;
    private ImageView profileImage, coverImage;
    private Button addPost, editProfile;
    private Uri profileUri;
    private StorageReference store = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference databaseRef = database.collection("USERS").document(auth.getUid());

    private String DATA_NAME,DATA_BATCH,DATA_DEPARTMENT,DATA_BIO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profile_name);
        bio = view.findViewById(R.id.profile_bio);
        batchName = view.findViewById(R.id.profile_batch);
        departmentName = view.findViewById(R.id.profile_department);
        profileImage = view.findViewById(R.id.profile_profile_image);
        coverImage = view.findViewById(R.id.profile_cover_image);
        addPost = view.findViewById(R.id.profile_add_postBTN);
        editProfile = view.findViewById(R.id.profile_editBTN);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Load All Data:
        loadData();
        //add post button clicked
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddPostActivity.class);
                startActivity(intent);
            }
        });
        //edit button clicked
        editProfile.setOnClickListener(v -> {
            Intent eIntent = new Intent(getActivity(), Setting.class);
            startActivity(eIntent);
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadProfileImage();
            }
        });

    }

    //Load Data start:------------------------->

    private void loadData() {

        databaseRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                DATA_NAME = documentSnapshot.getString("name");
                DATA_BIO = documentSnapshot.getString("bio");
                DATA_BATCH = documentSnapshot.getString("batch");
                DATA_DEPARTMENT = documentSnapshot.getString("department");
                name.setText(DATA_NAME);
                if (DATA_BIO != null){
                    bio.setText(DATA_BIO);
                }
                if (DATA_BATCH != null) {
                    batchName.setText(String.format("Batch Name: %s", DATA_BATCH));
                }
                if (DATA_DEPARTMENT != null) {
                    departmentName.setText(String.format("Department Name: %s", DATA_DEPARTMENT));
                }

                //Image Load
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_launcher_background);
                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(documentSnapshot.getString("profileUrl"))
                        .into(profileImage);

                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(documentSnapshot.getString("coverUrl"))
                        .into(coverImage);
            } else {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*----------Load Data End------------------*>

//    /*---------Profile Image Proceeding start---------------*/
////    private void uploadProfileImage() {
////        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
////            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
////                cropProfileImage();
////
////            } else {
////                cropProfileImage();
////                Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
////            }
////        } else {
////            cropProfileImage();
////        }
////    }
//    //------------Adding crop system------------
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        Toast.makeText(getActivity(), "Bal", Toast.LENGTH_SHORT).show();
////        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
////            CropImage.ActivityResult result = CropImage.getActivityResult(data);
////            if (resultCode == RESULT_OK) {
////                profileUri = result.getUri();
////                Toast.makeText(getActivity(), "This: "+profileUri, Toast.LENGTH_SHORT).show();
////                uploadProfileDatabase(profileUri);
////
////            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
////                Exception error = result.getError();
////                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
//    //Crop image method:----------->
//    private void cropProfileImage() {
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1, 1)
//                .start(getActivity());
//
//    }
//    //Upload Image on server:
//    private void uploadProfileDatabase(Uri profileUri) {
//        StorageReference profileStorage = store.child("Photo").child("Profile").child(auth.getUid()+".jpg");
//        ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Profile Image Uploading...");
//        progressDialog.show(); //Progress Dialog is created to loading
//        Toast.makeText(getActivity(), "Asche", Toast.LENGTH_SHORT).show();
//        if (profileUri != null){
//            Toast.makeText(getActivity(), "Dokche", Toast.LENGTH_SHORT).show();
//            profileStorage.putFile(profileUri)
//                    .addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if (task.isSuccessful()){
//                                profileStorage.getDownloadUrl().addOnCompleteListener(getActivity(),new OnCompleteListener<Uri>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//                                        String profileImageDownloadUrl = task.getResult().toString();
//                                        Map<String, Object> link = new HashMap<>();
//                                        link.put("profileUrl",profileImageDownloadUrl);
//                                        databaseRef.update(link)
//                                                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if (task.isSuccessful()){
//                                                            profileImage.setImageURI(profileUri);
//                                                            progressDialog.dismiss();
//                                                        }else{
//                                                            progressDialog.dismiss();
//                                                            Toast.makeText(getActivity(), "Error To update", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//                                                });
//                                    }
//                                });
//                            }else{
//                                progressDialog.dismiss();
//                                Toast.makeText(getActivity(), "Error To Upload", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//    }
//    /*---------Profile Image Proceeding End---------------*/

}
