package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mohammadkz.porsno_android.R;

public class MainPageActivity extends AppCompatActivity {

    DrawerLayout drawer_layout;
    MaterialToolbar topAppBar;
    NavigationView nav_view;
    BottomNavigationView bottom_nav;
    TextView name , phoneNumber;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        initViews();
        controllerViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.openDrawerContentRes, R.string.closeDrawerContentRes);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void initViews() {
        drawer_layout = findViewById(R.id.drawer_layout);
        topAppBar = findViewById(R.id.topAppBar);
        nav_view = findViewById(R.id.nav_view);
        bottom_nav = findViewById(R.id.bottom_nav);

        header = nav_view.getHeaderView(0);
        name = header.findViewById(R.id.user_name);
        phoneNumber = header.findViewById(R.id.phoneNumber);

    }

    private void controllerViews() {
        topAppBar.setNavigationOnClickListener(v -> {

            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START);
            } else {
                drawer_layout.openDrawer(GravityCompat.START);
            }

        });

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.myQuestion:
                        startFragment();
                        break;

                    case R.id.allQuestion:

                        break;
                }

                return true;
            }
        });
    }

    private void startFragment() {

    }

}