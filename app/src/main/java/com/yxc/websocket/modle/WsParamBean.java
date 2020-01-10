package com.yxc.websocket.modle;

import com.alibaba.fastjson.JSON;

public class WsParamBean {
    public WsParamBean(String pkey, String pdata) {
        this.pkey = pkey;
        this.pdata = pdata;
    }

    public WsParamBean() {
    }

    private Integer code;
    private String msg;
    private Integer sessionId;
    private String userName;
    private String pw;
    private String pkey;
    private String pdata;
    private String publicKey;
    private String token;
    private String otherToken;//对方的token
    private Integer otherCode;//对方的code

    private String uuid;

    private String verifiCode;

    private Integer status;//0 line 1 offline

    private Integer pageNo;
    private Integer pageSize;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
