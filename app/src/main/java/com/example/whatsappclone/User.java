package com.example.whatsappclone;

import java.util.ArrayList;

public class User {
    public String email;
    public String userId;
    public String phonenumber;

    public User(String email, String userId, String phonenumber) {
        this.email = email;
        this.userId = userId;
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
