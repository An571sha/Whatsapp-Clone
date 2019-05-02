package com.example.whatsappclone;

import java.util.ArrayList;

public class ChatBetweenUsers {

    public String messageId;
    public String userId;
    public String message;

    public ChatBetweenUsers(String messageId, String uId, String message) {
        this.messageId = messageId;
        this.userId = uId;
        this.message = message;
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


}
