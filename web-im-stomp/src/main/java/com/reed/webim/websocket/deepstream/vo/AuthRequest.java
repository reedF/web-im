package com.reed.webim.websocket.deepstream.vo;

import java.io.Serializable;

/**
 * deepstream auth request body
 * 
 *
 */
public class AuthRequest implements Serializable{
    private AuthData authData;
    private Object connectionData;

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    public Object getConnectionData() {
        return connectionData;
    }

    public void setConnectionData(Object connectionData) {
        this.connectionData = connectionData;
    }

}
