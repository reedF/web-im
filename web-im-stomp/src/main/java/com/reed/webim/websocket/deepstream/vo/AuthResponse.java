package com.reed.webim.websocket.deepstream.vo;

import java.io.Serializable;

/**
 * response for deepstram auth
 * @author reed
 *
 */
public class AuthResponse implements Serializable {

    private String username;

    private ClientData clientData;

    private ServerData serverData;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

}
