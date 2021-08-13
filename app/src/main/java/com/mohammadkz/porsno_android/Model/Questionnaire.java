package com.mohammadkz.porsno_android.Model;

import java.util.ArrayList;
import java.util.List;

public class Questionnaire {
    private String name, category, startDate, endDate, startDate_stamp, endDate_stamp, description, userId, views, done , Id;
    private List<Question> questions;
    private boolean expended;

    public Questionnaire() {
        questions = new ArrayList<>();
        expended = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate_stamp() {
        return startDate_stamp;
    }

    public void setStartDate_stamp(String startDate_stamp) {
        this.startDate_stamp = startDate_stamp;
    }

    public String getEndDate_stamp() {
        return endDate_stamp;
    }

    public void setEndDate_stamp(String endDate_stamp) {
        this.endDate_stamp = endDate_stamp;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isExpended() {
        return expended;
    }

    public void setExpended(boolean expended) {
        this.expended = expended;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
