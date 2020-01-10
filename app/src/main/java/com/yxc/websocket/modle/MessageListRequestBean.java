package com.yxc.websocket.modle;


public class MessageListRequestBean {
    private String token;
    private int otherCode;
    private int pageSize = 10;
    private int pageNo = 1;

    public MessageListRequestBean(String token, int otherCode, int pageSize, int pageNo) {
        this.token = token;
        this.otherCode = otherCode;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOtherCode() {
        return otherCode;
    }

    public void setOtherCode(int otherCode) {
        this.otherCode = otherCode;
    }
}
