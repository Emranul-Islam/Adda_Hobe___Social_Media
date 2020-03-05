package com.muhammad_sohag.socialmedia;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_frame_layout)
    protected FrameLayout frameLayout;
    @BindView(R.id.main_toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.main_net_connection)
    protected TextView netConnection;
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        //Net Connection verify:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobileNetwork.isConnected()) {
            netConnection.setVisibility(View.GONE);
        } else {
            netConnection.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Social Media");
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //Default Select Fragment:
        setFragment(new HomeFragment(), "Home");

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            item -> {
                //handle item click
                switch (item.getItemId()) {
                    case R.id.home_fragment:
                        //Home fragment select
                        setFragment(new HomeFragment(), "Home");
                        return true;
                    case R.id.profile_fragment:
                        //Profile Fragment select
                        setFragment(new ProfileFragment(), "Profile");
                        return true;
                    case R.id.users_fragment:
                        //All User Fragment select
                        setFragment(new UsersFragment(), "Users");
                        return true;
                }
                return false;
            };

    //Fragment Transaction Method:
    private void setFragment(Fragment fragment, String title) {
        getSupportActionBar().setTitle(title);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post:
                Intent addPostIntent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(addPostIntent);
                return true;
            case R.id.menu_logout:
                auth.signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent mIntent = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(mIntent);
                    finish();
                } //todo: ekhane ekta bug achce fix korte hobe

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
