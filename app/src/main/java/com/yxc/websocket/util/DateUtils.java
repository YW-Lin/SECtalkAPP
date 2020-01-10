package com.yxc.websocket.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {
    @SuppressLint("SimpleDateFormat")
    public static String getYMDHMS(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}
