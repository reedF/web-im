package com.reed.webim.websocket.stomp.message;

/**
 * Created by lincoln on 16-10-25
 */
public class ToUserMessage {
    private String userId;
    private String message;

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
