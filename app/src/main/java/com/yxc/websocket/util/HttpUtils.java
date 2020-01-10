package com.yxc.websocket.util;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


public class HttpUtils {
    public static void post(String url, String pdata, String pkey, StringCallback callback) {
        String baseUrl;
        String nativeUrl = SPUtils.getString("url", "");
        if (TextUtils.isEmpty(nativeUrl)) {
            baseUrl = Util.ws;
        } else {
            baseUrl = nativeUrl;
        }
        OkHttpUtils
                .post()
                .url(baseUrl + url)
                .addParams("pdata", pdata)
                .addParams("pkey", pkey)
                .build()
                .execute(callback);
    }
}
