package com.mohammadkz.porsno_android.Model.Response;

import com.google.gson.annotations.SerializedName;
import com.mohammadkz.porsno_android.Model.Question;
import com.mohammadkz.porsno_android.Model.Questionnaire;

import java.util.List;

public class GetQuestionResponse {
    @SerializedName("questionId")
    String questionId;
    @SerializedName("icon")
    String icon;
    @SerializedName("questionName")
    String questionName;
    @SerializedName("start")
    String start;
    @SerializedName("end")
    String end;
    @SerializedName("userId")
    String userId;
    @SerializedName("description")
    String description;
    @SerializedName("views")
    String views;
    @SerializedName("answers")
    String answers;
    @SerializedName("question")
    String question;

    boolean Expended;

    public GetQuestionResponse() {
        this.Expended = false;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public boolean isExpended() {
        return Expended;
    }

    public void setExpended(boolean expended) {
        Expended = expended;
    }
}
