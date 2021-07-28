package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.mohammadkz.porsno_android.Fragment.NewQuestion.NewQuestion_InfoFragment;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.warkiz.widget.IndicatorSeekBar;

import org.json.JSONObject;

public class NewQuestionActivity extends AppCompatActivity {

    IndicatorSeekBar seekBar;
    String Id;
    MaterialToolbar materialToolbar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        Id = getIntent().getStringExtra("id");

        initViews();
        controllerViews();
        getDate();
        startFragment();

    }

    private void initViews() {
        seekBar = findViewById(R.id.seekBar);
        materialToolbar = findViewById(R.id.topAppBar);
    }

    private void controllerViews() {
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setSeekBar(int pos) {
        switch (pos) {
            case 1:
                seekBar.setProgress(46);
                break;

            case 2:
                seekBar.setProgress(100);
                break;
        }
    }

    private void startFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewQuestion_InfoFragment newQuestion_infoFragment = new NewQuestion_InfoFragment(user);
        fragmentTransaction.replace(R.id.frameLayout, newQuestion_infoFragment).commit();

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