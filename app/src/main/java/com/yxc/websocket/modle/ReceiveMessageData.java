package com.yxc.websocket.modle;


public class ReceiveMessageData {


    private int acceptCode;
    private String acceptName;
    private long create;
    private String msg;
    private int sendCode;
    private String sendName;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAcceptCode() {
        return acceptCode;
    }

    public void setAcceptCode(int acceptCode) {
        this.acceptCode = acceptCode;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSendCode() {
        return sendCode;
    }

    public void setSendCode(int sendCode) {
        this.sendCode = sendCode;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }
}
