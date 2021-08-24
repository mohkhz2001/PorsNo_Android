package com.mohammadkz.porsno_android.Fragment.NewQuestion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohammadkz.porsno_android.R;


public class NewQuestion_DateFragment extends Fragment {

    View view;

    public NewQuestion_DateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question_date, container, false);

        initViews();
        controllerViews();

        return view;
    }

    private void initViews(){

    }

    private void controllerViews(){

    }
}