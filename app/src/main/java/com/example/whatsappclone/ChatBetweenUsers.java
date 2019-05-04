package com.example.whatsappclone;

import java.util.ArrayList;

public class ChatBetweenUsers {

    private String messageId;
    private String userId;
    private String message;
    private Boolean showEmail = true;
    private String email;

    public ChatBetweenUsers() {}

    public ChatBetweenUsers(String messageId, String uId, String message, String email) {
        this.messageId = messageId;
        this.userId = uId;
        this.message = message;
        this.email = email;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean getShowEmail() {
        return showEmail;
    }

    public void setShowEmail(Boolean showEmail) {
        this.showEmail = showEmail;
    }

}
