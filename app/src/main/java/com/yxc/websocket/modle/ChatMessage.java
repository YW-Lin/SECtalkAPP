package com.yxc.websocket.modle;

public class ChatMessage {
    private int isMeSend;
    public int getIsMeSend() {
        return isMeSend;
    }

    public void setIsMeSend(int isMeSend) {
        this.isMeSend = isMeSend;
    }
    private String code;
    private long create;
    private int sendCode;
    private String sendName;
    private int acceptCode;
    private String acceptName;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
