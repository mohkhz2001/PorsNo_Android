package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.Fragment.ContactUsFragment;
import com.mohammadkz.porsno_android.Fragment.HistoryFragment;
import com.mohammadkz.porsno_android.Fragment.MainFragment;
import com.mohammadkz.porsno_android.Fragment.MyQuestionFragment;
import com.mohammadkz.porsno_android.Fragment.ProfileFragment;
import com.mohammadkz.porsno_android.Fragment.ReBuyFragment;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;

import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class MainPageActivity extends AppCompatActivity {

    DrawerLayout drawer_layout;
    MaterialToolbar topAppBar;
    NavigationView nav_view;

    TextView name, phoneNumber;
    View header;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_page);

            initViews();
            getDate();
            controllerViews();
            startFragment();

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.openDrawerContentRes, R.string.closeDrawerContentRes);
            drawer_layout.addDrawerListener(toggle);
            toggle.syncState();
        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "main page Activity - create");
            onCreate(savedInstanceState);
        }

    }

    private void initViews() {
        drawer_layout = findViewById(R.id.drawer_layout);
        topAppBar = findViewById(R.id.topAppBar);
        nav_view = findViewById(R.id.nav_view);

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

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.main:
                        nav_view.setCheckedItem(R.id.main);
                        startFragment();
                        break;
                    case R.id.newQuestion:
                        nav_view.setCheckedItem(R.id.newQuestion);
                        Intent intent = new Intent(getApplicationContext(), NewQuestionActivity.class);
                        Gson gson = new Gson();
                        String a = gson.toJson(user);
                        intent.putExtra("userInfo", a);//user.getID()
                        startActivity(intent);
                        break;
                    case R.id.account:
                        setTopAppBar("حساب کاربری");
                        nav_view.setCheckedItem(R.id.account);
                        ProfileFragment profileFragment = new ProfileFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout, profileFragment).commit();
                        break;
                    case R.id.reBuy:
                        setTopAppBar("تمدید حساب");
                        nav_view.setCheckedItem(R.id.reBuy);
                        ReBuyFragment reBuyFragment = new ReBuyFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout, reBuyFragment).commit();
                        break;
                    case R.id.history:
                        setTopAppBar("تاریخچه");
                        nav_view.setCheckedItem(R.id.history);
                        HistoryFragment historyFragment = new HistoryFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout, historyFragment).commit();
                        break;
                    case R.id.contact:
                        setTopAppBar("راه ارتباطی");
                        nav_view.setCheckedItem(R.id.contact);
                        ContactUsFragment contactUsFragment = new ContactUsFragment();
                        fragmentTransaction.replace(R.id.frameLayout, contactUsFragment).commit();
                        break;
                    case R.id.exit:
                        removeSharedPreferences();
                        finish();
                        break;
                }

                drawer_layout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void startFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragment mainFragment = new MainFragment(user);
        fragmentTransaction.replace(R.id.frameLayout, mainFragment).commit();
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
                user.setQuestionRemaining(jsonObject.getString("questionRemaining"));

                if (jsonObject.getString("birthdayDate") != null) {
                    user.setBirthdayDate(jsonObject.getString("birthdayDate"));
                }

                if (jsonObject.getString("accountLevel").equals("Bronze")) {
                    user.setAccountLevel(StaticFun.account.Bronze);
                } else if (jsonObject.getString("accountLevel").equals("Steel")) {
                    user.setAccountLevel(StaticFun.account.Steel);
                } else if (jsonObject.getString("accountLevel").equals("Gold")) {
                    user.setAccountLevel(StaticFun.account.Gold);
                } else if (jsonObject.getString("accountLevel").equals("Diamond")) {
                    user.setAccountLevel(StaticFun.account.Diamond);
                }

                setHeaderValue();

            } catch (Exception e) {
                e.printStackTrace();
                Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "main page Activity - get data");
            }
    }

    private void setHeaderValue() {
        name.setText(user.getName().toString());
        phoneNumber.setText(user.getPn().toString());
    }

    public void updateUser(User user) {
        name.setText(user.getName().toString());
        this.user = user;
    }

    public void setDrawerSelect(int id) {
        nav_view.setCheckedItem(id);
    }

    private void removeSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("userLogin_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setTopAppBar(String name) {
        topAppBar.setTitle(name);
    }
}