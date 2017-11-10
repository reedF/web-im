package com.reed.webim.websocket.deepstream.vo;

import java.io.Serializable;
import java.util.Date;

public class ClientData implements Serializable {

    private String color = "blue";

    private Date loginTime = new Date();

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

}
