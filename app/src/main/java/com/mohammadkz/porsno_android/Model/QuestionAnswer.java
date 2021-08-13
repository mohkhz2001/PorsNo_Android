package com.mohammadkz.porsno_android.Model;

public class QuestionAnswer extends Answer{

    private String questionNumber;

    public QuestionAnswer(String answer , String questionNumber) {
        super(answer);
        this.questionNumber = questionNumber;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }
}
