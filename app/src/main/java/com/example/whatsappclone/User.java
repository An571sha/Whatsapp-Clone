package com.example.whatsappclone;

import java.util.ArrayList;

public class User {
    public String email;
    public ArrayList<String> friendsListByUserId;
    public String phonenumber;

    public User(String email, ArrayList friendsListByUserId, String phonenumber) {
        this.email = email;
        this.friendsListByUserId = friendsListByUserId;
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getFriendsListByUserId() {
        return friendsListByUserId;
    }

    public void setFriendsListByUserId(ArrayList<String> friendsListByUserId) {
        this.friendsListByUserId = friendsListByUserId;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
