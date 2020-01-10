package com.yxc.websocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yxc.websocket.R;
import com.yxc.websocket.modle.LoginBean;
import com.yxc.websocket.modle.request.UpdateSessionRequest;
import com.yxc.websocket.util.DialogUtils;
import com.yxc.websocket.util.HttpUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.yxc.websocket.util.UUIDUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class LaucherActivity extends AppCompatActivity {

    private String token;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(LaucherActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    Intent intent2 = new Intent(LaucherActivity.this, MessageListActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher);
        getSupportActionBar().hide();

        token = SPUtils.getString("token", "");
        getAseKey();
        getPublicKey();
    }

    private void getAseKey() {
        String uuid = UUIDUtils.getUUID(12);
        SPUtils.put("aes_key", uuid);
    }

    private void getPublicKey() {
        final String token = SPUtils.getString("token", "");
        UpdateSessionRequest request = new UpdateSessionRequest(0, token);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/rsa/getPublicKey", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(LaucherActivity.this, e.getMessage().toString());
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                LoginBean bean = JSON.parseObject(response, new TypeReference<LoginBean>() {
                });
                if ("200".equals(bean.getCode())) {
                    SPUtils.put("publicKey", bean.getData());
                    if (TextUtils.isEmpty(token)) {
                        handler.sendEmptyMessage(0);
                    } else {
                        autoLogin();
                    }
                } else {
                    DialogUtils.OneButtonDialog(LaucherActivity.this, bean.getMsg());
                }
            }
        });
    }

    private void autoLogin() {
        String token = SPUtils.getString("token", "");
        UpdateSessionRequest request = new UpdateSessionRequest(0, token);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("/api/ws/autoLogin", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(LaucherActivity.this, e.getMessage().toString());
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                LoginBean bean = JSON.parseObject(response, new TypeReference<LoginBean>() {
                });
                if ("200".equals(bean.getCode())) {
                    handler.sendEmptyMessage(1);
                } else if ("100".equals(bean.getCode())) {
                    handler.sendEmptyMessage(0);
                } else {
                    DialogUtils.OneButtonDialog(LaucherActivity.this, bean.getMsg());
                }
            }
        });
    }

}
