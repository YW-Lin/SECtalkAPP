package com.yxc.websocket.modle.request;


public class CheckRequest {
    private String data;

    public CheckRequest(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
