package com.mohammadkz.porsno_android.Fragment.ManageQuestionnaire;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohammadkz.porsno_android.Adapter.ConfirmNewQuestionaireAdapter;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.SweetDialog;
import com.mohammadkz.porsno_android.R;

public class Manager_questionsFragment extends Fragment {

    View view;
    RecyclerView list;
    Questionnaire questionnaire;
    ConfirmNewQuestionaireAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    public Manager_questionsFragment(Questionnaire questionnaire) {
        // Required empty public constructor
        this.questionnaire = questionnaire;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manager_questions, container, false);

        initViews();
        controllerViews();
        setAdapter();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
    }

    private void controllerViews() {

    }

    private void setAdapter() {
        adapter = new ConfirmNewQuestionaireAdapter(getContext(), questionnaire.getQuestions(), false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(linearLayoutManager);

        list.setAdapter(adapter);

    }

}