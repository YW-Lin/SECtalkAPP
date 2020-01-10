package com.yxc.websocket.modle;


public class PdataBean {


    private int pageNo;
    private int pageSize;
    private String pdata;
    private String pkey;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPdata() {
        return pdata;
    }

    public void setPdata(String pdata) {
        this.pdata = pdata;
    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }
}
