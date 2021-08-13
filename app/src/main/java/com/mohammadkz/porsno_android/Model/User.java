package com.mohammadkz.porsno_android.Model;

import com.mohammadkz.porsno_android.StaticFun;

public class User {
    private String ID, name, pn, pwd, createdTime, endTime , birthdayDate;
    private StaticFun.account accountLevel;

    public User(String name, String pn, String pwd) {
        this.name = name;
        this.pn = pn;
        this.pwd = pwd;
    }

    public User() {
    }

    public String getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public StaticFun.account getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(StaticFun.account accountLevel) {
        this.accountLevel = accountLevel;
    }
}
