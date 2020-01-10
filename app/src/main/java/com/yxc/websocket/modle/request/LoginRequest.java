package com.yxc.websocket.modle.request;


public class LoginRequest {
    private String userName;
    private String pw;
    private String uuid;
    private String verifiCode;

    public LoginRequest(String userName, String pw, String uuid, String verifiCode) {
        this.userName = userName;
        this.pw = pw;
        this.uuid = uuid;
        this.verifiCode = verifiCode;
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
}
