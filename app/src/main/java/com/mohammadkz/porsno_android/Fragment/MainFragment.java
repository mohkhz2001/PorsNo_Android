package com.mohammadkz.porsno_android.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.Activity.MainPageActivity;
import com.mohammadkz.porsno_android.Activity.NewQuestionActivity;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;


public class MainFragment extends Fragment {

    View view;
    User user;
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    int location;
    boolean visibility = false;

    public MainFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_main, container, false);

            initViews();
            controllerViews();
            startFragment();

            return view;
        } catch (Exception e) {
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "main fragment - create");
            return view;
        }

    }

    private void initViews() {
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        fab = view.findViewById(R.id.fab);
    }

    private void controllerViews() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.myQuestion:
                        setFabIcon(R.id.myQuestion);
                        startFragment();
                        break;
                    case R.id.allQuestion:
                        location = R.id.allQuestion;
                        setFabIcon(R.id.allQuestion);
                        ((MainPageActivity) getActivity()).setTopAppBar("تمام پرسشنامه ها");
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        AllQuestionFragment allQuestionFragment = new AllQuestionFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout_main, allQuestionFragment).commit();
                        break;
                }

                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location == R.id.myQuestion) {
                    newQuestion();
                } else {
                    // fragment communication
                    Bundle bundle = new Bundle();
                    bundle.putString("visibility", visibility ? "false" : "true");
                    visibility = !visibility;
                    getParentFragmentManager().setFragmentResult("visibility_data", bundle);
                }
            }
        });

        getParentFragmentManager().setFragmentResultListener("visibility_t", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.e("test data _", result.getString("visibility").toString());
                boolean visibility_ = result.getString("visibility") == "false";
                visibility = false;
            }
        });
    }

    private void startFragment() {
        ((MainPageActivity) getActivity()).setTopAppBar("پرسشنامه های من");
        location = R.id.myQuestion;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        MyQuestionFragment myQuestionFragment = new MyQuestionFragment(user);
        fragmentTransaction.replace(R.id.frameLayout_main, myQuestionFragment).commit();
    }

    private void setFabIcon(int location) {
        if (location == R.id.allQuestion) {
            fab.setImageResource(R.drawable.ic_filter);
        } else {
            fab.setImageResource(R.drawable.plus);
        }
    }

    private void newQuestion() {
        Intent intent = new Intent(getContext(), NewQuestionActivity.class);
        Gson gson = new Gson();
        String a = gson.toJson(user);
        intent.putExtra("userInfo", a);//user.getID()
        startActivity(intent);
    }
}