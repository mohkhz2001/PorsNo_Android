package com.mohammadkz.porsno_android.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;


public class MainFragment extends Fragment {

    View view;
    User user;
    BottomNavigationView bottom_nav;

    public MainFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        initViews();
        controllerViews();
        startFragment();

        return view;
    }

    private void initViews() {
        bottom_nav = view.findViewById(R.id.bottom_nav);
    }

    private void controllerViews() {
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.myQuestion:
                        startFragment();
                        break;

                    case R.id.allQuestion:
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        AllQuestionFragment allQuestionFragment = new AllQuestionFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout_main, allQuestionFragment).commit();
                        break;
                }

                return true;
            }
        });
    }

    private void startFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        MyQuestionFragment myQuestionFragment = new MyQuestionFragment(user);
        fragmentTransaction.replace(R.id.frameLayout_main, myQuestionFragment).commit();
    }
}