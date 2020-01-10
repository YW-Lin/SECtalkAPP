package com.yxc.websocket.modle.request;


public class UpdateSessionRequest {
    private int sessionId;
    private String token;

    public UpdateSessionRequest(int sessionId, String token) {
        this.sessionId = sessionId;
        this.token = token;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
