package com.yxc.websocket.modle.request;


public class SendSessionRequest {
    private String token;
    private String sessionId;

    public SendSessionRequest(String token, String sessionId) {
        this.token = token;
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
