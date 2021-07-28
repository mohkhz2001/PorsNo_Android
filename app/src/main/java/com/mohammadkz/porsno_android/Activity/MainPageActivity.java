package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mohammadkz.porsno_android.Fragment.MyQuestionFragment;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;

import org.json.JSONObject;

public class MainPageActivity extends AppCompatActivity {

    DrawerLayout drawer_layout;
    MaterialToolbar topAppBar;
    NavigationView nav_view;
    BottomNavigationView bottom_nav;
    TextView name, phoneNumber;
    View header;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        initViews();
        getDate();
        controllerViews();
        startFragment();

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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MyQuestionFragment myQuestionFragment = new MyQuestionFragment(user);
        fragmentTransaction.replace(R.id.frameLayout, myQuestionFragment).commit();
    }

    // get data from sign up page(intent)
    private void getDate() {
        String data = getIntent().getStringExtra("userInfo");
        Log.e("user", " " + data);
        if (data != null)
            try {
                JSONObject jsonObject = new JSONObject(data);

                user = new User();
                user.setID(jsonObject.getString("ID"));
                user.setPn(jsonObject.getString("pn"));
                user.setName(jsonObject.getString("name"));
                user.setCreatedTime(jsonObject.getString("createdTime"));
                user.setEndTime(jsonObject.getString("endTime"));

                // account level ....

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}