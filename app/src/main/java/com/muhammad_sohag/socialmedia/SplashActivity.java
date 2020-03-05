package com.muhammad_sohag.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);


  }

  @Override
  protected void onStart() {
    super.onStart();
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
          Thread.sleep(1000);
          if (user != null) {
            Intent mIntent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(mIntent);
            finish();
          }else{
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();

  }
}
