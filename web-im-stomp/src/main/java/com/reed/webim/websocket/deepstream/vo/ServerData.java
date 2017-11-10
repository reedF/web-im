package com.reed.webim.websocket.deepstream.vo;

public class ServerData {

    private String role = "Admin";
    private String permission = "*";

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

}
