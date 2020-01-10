package com.yxc.websocket.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.flyco.dialog.listener.OnBtnClickL;
import com.yxc.websocket.R;
import com.yxc.websocket.modle.CheckBean;
import com.yxc.websocket.modle.LoginBean;
import com.yxc.websocket.modle.request.CheckRequest;
import com.yxc.websocket.modle.request.UpdateSessionRequest;
import com.yxc.websocket.util.DialogUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.yxc.websocket.util.UUIDUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_setting_address;
    private Button btn_setting_save;
    private ImageView iv_setting_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        initView();
    }

    private void initView() {
        btn_setting_save = (Button) findViewById(R.id.btn_setting_save);
        et_setting_address = (EditText) findViewById(R.id.et_setting_address);
        iv_setting_return = (ImageView) findViewById(R.id.iv_setting_return);
        iv_setting_return.setOnClickListener(this);
        btn_setting_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting_save:
                getPublicKey("http://" + et_setting_address.getText().toString() + "/");
                break;
            case R.id.iv_setting_return:
                finish();
                break;
        }
    }

    private void getPublicKey(final String baseUrl) {
        String token = SPUtils.getString("token", "");
        UpdateSessionRequest request = new UpdateSessionRequest(0, token);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        OkHttpUtils.post().url(baseUrl + "api/rsa/getPublicKey").addParams("pdata", pdata).addParams("pkey", pkey).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(SettingActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                LoginBean bean = JSON.parseObject(response, new TypeReference<LoginBean>() {
                });
                if ("200".equals(bean.getCode())) {
                    SPUtils.put("publicKey", bean.getData());
                    checkPublicKey(baseUrl, bean.getData());
                } else {
                    DialogUtils.OneButtonDialog(SettingActivity.this, bean.getMsg());
                }
            }
        });
    }

    private void checkPublicKey(final String baseUrl, final String publicKey) {
        final String uuid = getUUID();
        CheckRequest request = new CheckRequest(uuid);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        OkHttpUtils.post().url(baseUrl + "api/rsa/de4privateKey").addParams("pdata", pdata).addParams("pkey", pkey).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(SettingActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                CheckBean checkRequest = JSON.parseObject(response, new TypeReference<CheckBean>() {
                });
                if ("200".equals(checkRequest.getCode())) {
                    if (checkRequest.getData().contains(uuid)) {
                        SPUtils.put("publicKey", publicKey);
                        SPUtils.put("url", baseUrl);
                        DialogUtils.OneButtonDialog(SettingActivity.this, "Replace the success", new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                finish();
                            }
                        });
                    } else {
                        DialogUtils.OneButtonDialog(SettingActivity.this, "Replacement failed, please try again");
                    }
                } else {
                    DialogUtils.OneButtonDialog(SettingActivity.this, checkRequest.getMsg());
                }


            }
        });
    }

    private String getUUID() {
        return UUIDUtils.getUUID(6);
    }
}
