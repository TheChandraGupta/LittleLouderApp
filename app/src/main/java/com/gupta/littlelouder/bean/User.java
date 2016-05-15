package com.gupta.littlelouder.bean;

/**
 * Created by GUPTA on 14-May-16.
 */
public class User {

    private int userId = 0;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String type = "USER";
    private String dOJ = "NA";
    private String remember = "true";

    public User() {
    }

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(int userId, String name, String email, String phone, String password, String type, String dOJ, String remember) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.type = type;
        this.dOJ = dOJ;
        this.remember = remember;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getdOJ() {
        return dOJ;
    }

    public void setdOJ(String dOJ) {
        this.dOJ = dOJ;
    }

    public String getRemember() {
        return remember;
    }

    public void setRemember(String remember) {
        this.remember = remember;
    }
}
