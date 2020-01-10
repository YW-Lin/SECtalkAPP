package com.yxc.websocket.modle.request;


public class RegisterRequest {
    private String userName;
    private String pw;
    private String sessionId;
    private String publicKey;
    private String verifiCode;
    private String uuid;

    public RegisterRequest(String userName, String pw, String sessionId, String publicKey, String verifiCode, String uuid) {
        this.userName = userName;
        this.pw = pw;
        this.sessionId = sessionId;
        this.publicKey = publicKey;
        this.verifiCode = verifiCode;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVerifiCode() {
        return verifiCode;
    }

    public void setVerifiCode(String verifiCode) {
        this.verifiCode = verifiCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
