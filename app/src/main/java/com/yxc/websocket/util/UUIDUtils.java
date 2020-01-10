package com.yxc.websocket.util;

import java.util.UUID;


public class UUIDUtils {
    public static String getUUID(Integer length) {
        String replace = UUID.randomUUID().toString().replace("-", "");
        return replace.substring(replace.length() - length < 0 ? 0 : replace.length() - length);
    }
}
