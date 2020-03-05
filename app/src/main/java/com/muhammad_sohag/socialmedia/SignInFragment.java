package com.muhammad_sohag.socialmedia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAccount;
    private FrameLayout frameLayout;
    private EditText email;
    private EditText pass;
    private Button signInBtn;
    private ProgressBar signInProgress;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAccount = view.findViewById(R.id.sign_in_creat_account);
        frameLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.register_frame_layout);
        email = view.findViewById(R.id.sign_in_email);
        pass  = view.findViewById(R.id.sign_in_pass);
        signInBtn = view.findViewById(R.id.sign_in_btn);
        signInProgress = view.findViewById(R.id.sign_in_progressbar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
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

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInProgress.setVisibility(View.VISIBLE);
                signInWork();
            }
        });



    }

    //Sign in button All work method:
    private void signInWork() {
        auth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            signInProgress.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity(),MainActivity.class);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).finish();
                        }else {
                            signInProgress.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Error: "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    //Check method:
    private void checkFields() {
        if (!TextUtils.isEmpty(email.getText())){
            if (!TextUtils.isEmpty(pass.getText())){
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(Color.rgb(250,250,250));
            }else{
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,250,250,250));
            }
        }else{
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50,250,250,250));
        }
    }

    //set fragment method:
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

}
