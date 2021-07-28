package com.mohammadkz.porsno_android.Model;

import java.util.ArrayList;
import java.util.List;

public class Question {
    String question;
    List<Answer> answers;
    boolean test;

    public Question() {
        answers = new ArrayList<>();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }
}
