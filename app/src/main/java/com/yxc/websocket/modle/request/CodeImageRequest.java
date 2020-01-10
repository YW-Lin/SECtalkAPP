package com.yxc.websocket.modle.request;


public class CodeImageRequest {
    private String uuid;

    public CodeImageRequest(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
