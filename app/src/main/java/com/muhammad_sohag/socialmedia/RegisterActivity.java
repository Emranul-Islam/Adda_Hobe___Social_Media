package com.muhammad_sohag.socialmedia;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RegisterActivity extends AppCompatActivity {


    private FrameLayout frameLayout;
    private TextView netConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        checkConnection();

        frameLayout = findViewById(R.id.register_frame_layout);
        setFragment(new SignInFragment());

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


    private void checkConnection() {
        //Net Connection verify:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        netConnection = findViewById(R.id.reg_net_connection);
        if (wifi.isConnected() || mobileNetwork.isConnected()) {
            netConnection.setVisibility(View.GONE);

        } else {
            netConnection.setVisibility(View.VISIBLE);
        }

    }


}
