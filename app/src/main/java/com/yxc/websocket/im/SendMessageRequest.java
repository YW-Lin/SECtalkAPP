package com.yxc.websocket.im;


public class SendMessageRequest {
    private int otherCode;
    private String token;
    private String msg;

    public SendMessageRequest(int otherCode, String token, String msg) {
        this.otherCode = otherCode;
        this.token = token;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getOtherCode() {
        return otherCode;
    }

    public void setOtherCode(int otherCode) {
        this.otherCode = otherCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
