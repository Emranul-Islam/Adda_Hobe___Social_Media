package com.muhammad_sohag.socialmedia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.muhammad_sohag.socialmedia.R.id.register_frame_layout;

public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }
    private static final String TAG ="SignUpFragment";

    private TextView haveAccount;
    private EditText name;
    private EditText email;
    private EditText pass;
    private EditText confirmPass;
    private Button signUpBtn;
    private ProgressBar signUpProgress;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        haveAccount = view.findViewById(R.id.sign_up_login);
        name = view.findViewById(R.id.sign_up_name);
        email = view.findViewById(R.id.sign_up_email);
        pass = view.findViewById(R.id.sign_up_pass);
        confirmPass = view.findViewById(R.id.sign_up_con_pass);
        signUpBtn = view.findViewById(R.id.sign_up_btn);
        signUpBtn.setEnabled(false);
        signUpProgress = view.findViewById(R.id.sign_up_progressbar);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        //Text Change Watcher
        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (email.getText().toString().matches(emailPattern)){
                    Log.d(TAG, "afterTextChanged: Email Valid");
                }else{
                    email.setError("Email Not Valid");
                }
            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pass.length() >= 6){
                    Log.d(TAG, "afterTextChanged: Complet");
                }else {
                    pass.setError("Minimum 6 Digit");
                }
            }
        });
        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pass.getText().toString().equals(confirmPass.getText().toString())){
                    Log.d(TAG, "afterTextChanged: Match");
                }
                else {
                    confirmPass.setError("Password Not Match");
                }
            }
        });

        //Button Click
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchPass();
            }
        });

    }

    //Button Click to Check Pass Match:
    private void matchPass() {

        signUpBtn.setEnabled(false);
        signUpBtn.setTextColor(Color.argb(50,250,250,250));

        if (email.getText().toString().matches(emailPattern)){
            if (pass.getText().toString().equals(confirmPass.getText().toString())){
                signUpProgress.setVisibility(View.VISIBLE);
                //Crating account method call:
                createAccount();

            }else{
                confirmPass.setError("Password not matched");
                signUpBtn.setEnabled(false);
                signUpProgress.setVisibility(View.GONE);
                signUpBtn.setTextColor(Color.argb(50,250,250,250));
            }
        }else {
            email.setError("Email Not Valid");
            signUpBtn.setEnabled(false);
            signUpProgress.setVisibility(View.GONE);
            signUpBtn.setTextColor(Color.argb(50,250,250,250));
        }

    }

    //Setup Fragment Method:
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(register_frame_layout,fragment);
        fragmentTransaction.commit();
    }

    //Checking Every Fields are not Empty Method:
    @SuppressLint("ResourceAsColor")
    private void checkFields() {

        if (!TextUtils.isEmpty(name.getText())){
            if (!TextUtils.isEmpty(email.getText())){
                if (!TextUtils.isEmpty(pass.getText())){
                    if (!TextUtils.isEmpty(confirmPass.getText())){
                        signUpBtn.setTextColor(Color.rgb(255,255,255));
                        signUpBtn.setEnabled(true);
                    }else{
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(R.color.colorPrimaryDark);
                    }
                }else {

                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(R.color.colorPrimaryDark);
                }
            }else{
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(R.color.colorPrimaryDark);
            }
        }else {
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(R.color.colorPrimaryDark);
        }
    }

    //Creating Account Method:
    private void createAccount() {
        auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Map<String,Object> value = new HashMap<>();
                            value.put("name",name.getText().toString());
                            value.put("email",email.getText().toString());
                            String userId = auth.getUid();
                            if (userId != null) {
                                DocumentReference databaseRef = database.collection("USERS").document(userId);
                                databaseRef.set(value)
                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    signUpProgress.setVisibility(View.GONE);
                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                } else {
                                                    Log.d("SignUpFragment", "Adding Value:failure", task.getException());
                                                    Toast.makeText(getActivity(), "Error to add user Value", Toast.LENGTH_SHORT).show();
                                                    signUpProgress.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            }else {
                                Toast.makeText(getActivity(), "Error to Add user info", Toast.LENGTH_SHORT).show();
                                signUpProgress.setVisibility(View.GONE);
                            }
                        }else {
                            Log.e(TAG, "onComplete: Failed=" + Objects.requireNonNull(task.getException()).getMessage());
                            Toast.makeText(getActivity(), "Error to create account", Toast.LENGTH_SHORT).show();
                            signUpProgress.setVisibility(View.GONE);

                        }
                    }
                });
    }

}
