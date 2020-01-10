package com.yxc.websocket.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yxc.websocket.rsa.AESUtils;
import com.yxc.websocket.rsa.Base64Utils;
import com.yxc.websocket.rsa.RSAUtils;

import java.security.GeneralSecurityException;

/**
 * Created by cocoon on 2019/11/6 0006.
 */

public class SignUtils {

    private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDosvH1gCpQTTZLXGMcSeeqDjWuDVY0+Aab1VbtGJqWdkPd32D4hEUwFjVJ+FJbq7UpvFFDQ3k2y2n/1rzxWapFk/e+BNNCSKP9e6+Of1SLs83So27dgiAeAKmdQoxwfXrgvP1/QRMJJ0i6m3CRRyTlXO+cMGbYqRv1iTT9uaRolQIDAQAB";
    private static final String DEFAULT_PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOiy8fWAKlBNNktcYxxJ56oONa4NVjT4BpvVVu0YmpZ2Q93fYPiERTAWNUn4UlurtSm8UUNDeTbLaf/WvPFZqkWT974E00JIo/17r45/VIuzzdKjbt2CIB4AqZ1CjHB9euC8/X9BEwknSLqbcJFHJOVc75wwZtipG/WJNP25pGiVAgMBAAECgYEAv4PXY8hyCtkhYHDPGU8yHWHIiFFtq/ad6c9x1X00bbU0Mf1Q3/hswSDmBtUbY1s0pP7amtODhbdwrCFeK/0yBrOegb2fQeJs/QL6/y4/DPzRB21k9N8cQjgmv5tQb72fwdY8nDROXnzKQceMo6b/xkWaIhvhdUq6nCqPvoIGRIECQQD+lOKTQk769G9BQd7HW+2H2NioPbxri+V27daC1M5uBfBj8Wt3NDJ5IyMvOHz5yTlm8FsE2Zz1/aFdLJ/Rv4IRAkEA6f7ZOMcuxlRsAiN708+r3q3sxAyBood+qAJ1MKhOrdR94RcAPUkcjFTZ8j1v0eclj6+w2RChcpb5Ath93ia6RQJBAP3b6x+axHUcn4A8NfEn6vFGu6zwet3nT3bLbddia0JtK6wNhfMFGruO3TvuITlXfaT3UlvAv/LP6kOmBuw6AnECQQDR3r29awjM4ZMuJ908EJs6Ugx1mjH7MEOtNOcfCRXoWxm79QFF9nkgdEo2NlxAi2zo/s9DIONs/3O/1aSux1VxAkBkkOdc0f2ogWZHqtCYfVfYjwbMvlW/6lnbq0B76V1SVqogoSubwnF7EUBdmqpzWmzqM4xURBh9QqDnUUfBzPMW";

    public static String signPdata(Object bean) {
        String encrypt = null;
        try {
            String json = JSON.toJSONString(bean);
            String aes_key = SPUtils.getString("aes_key", "123456789111");
            encrypt = AESUtils.encrypt(aes_key, json);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encrypt;
    }

    public static String signPkey() {
        String aes_key = SPUtils.getString("aes_key", "123456789111");
        String byte2Base64 = null;
        try {
            byte[] data = aes_key.getBytes();
//            byte[] encodedData = RSAUtils.encryptByPublicKey(data, DEFAULT_PUBLIC_KEY);
            String publicKey = SPUtils.getString("publicKey", "");
            byte[] encodedData = RSAUtils.encryptByPublicKey(data, TextUtils.isEmpty(publicKey) ? DEFAULT_PUBLIC_KEY : publicKey);
            byte2Base64 = Base64Utils.encode(encodedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byte2Base64;
    }

    public static String decodeToJson(String data, String pkey) {
        String target = null;
        byte[] decode;
        try {
            String publicKey = SPUtils.getString("publicKey", "");
            byte[] tkey = RSAUtils.decryptByPublicKey(Base64Utils.decode(pkey), TextUtils.isEmpty(publicKey) ? DEFAULT_PUBLIC_KEY : publicKey);
            String encode = Base64Utils.encode(tkey);
            String decrypt = AESUtils.decrypt(encode, data);
            target = decrypt;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return target;
    }
}
