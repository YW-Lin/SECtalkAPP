package com.yxc.websocket.util;

import android.content.Context;
import android.widget.Toast;

public class Util {
//    public static final String ws = "http://116.62.12.140:8080/";
    public static final String ws = "http://192.168.0.160:8080/";

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
