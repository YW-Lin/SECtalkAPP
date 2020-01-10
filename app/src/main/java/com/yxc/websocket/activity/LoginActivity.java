package com.yxc.websocket.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.yxc.websocket.R;
import com.yxc.websocket.modle.LoginBean;
import com.yxc.websocket.modle.request.LoginRequest;
import com.yxc.websocket.util.DialogUtils;
import com.yxc.websocket.util.HttpUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.yxc.websocket.util.UUIDUtils;
import com.yxc.websocket.util.Util;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_login_id;
    private EditText et_login_password;
    private EditText et_login_verification_code;
    private Button btn_login_login;
    private Button btn_login_register;
    private ImageView iv_login_verification_code;
    private String uuid;
    private LinearLayout ll_login_parent;
    private TextView tv_login_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCodeImage();
    }

    private void initView() {
        et_login_id = (EditText) findViewById(R.id.et_login_id);
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);
        btn_login_register = (Button) findViewById(R.id.btn_login_register);
        et_login_verification_code = (EditText) findViewById(R.id.et_login_verification_code);
        iv_login_verification_code = (ImageView) findViewById(R.id.iv_login_verification_code);
        ll_login_parent = (LinearLayout) findViewById(R.id.ll_login_parent);
        tv_login_setting = (TextView) findViewById(R.id.tv_login_setting);

        btn_login_login.setOnClickListener(this);
        btn_login_register.setOnClickListener(this);
        ll_login_parent.setOnClickListener(this);
        tv_login_setting.setOnClickListener(this);
        iv_login_verification_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_register:
                goToRegister();
                break;
            case R.id.btn_login_login:
                login();
                break;
            case R.id.ll_login_parent:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ll_login_parent.getWindowToken(), 0);
                break;
            case R.id.tv_login_setting:
                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_login_verification_code:
                getCodeImage();
                break;
        }
    }

    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        if (TextUtils.isEmpty(et_login_id.getText().toString())) {
            DialogUtils.OneButtonDialog(LoginActivity.this, "Please enter your ID");
            return;
        }
        if (TextUtils.isEmpty(et_login_password.getText().toString())) {
            DialogUtils.OneButtonDialog(LoginActivity.this, "Please enter your password");
            return;
        }
        LoginRequest request = new LoginRequest(et_login_id.getText().toString(), et_login_password.getText().toString(), uuid, et_login_verification_code.getText().toString());
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/ws/userLogin", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(LoginActivity.this, e.getMessage().toString());
            }

            @Override
            public void onResponse(String response, int id) {
                LoginBean bean = JSON.parseObject(response, new TypeReference<LoginBean>() {
                });
                if ("200".equals(bean.getCode())) {
                    SPUtils.put("token", bean.getData());
                    SPUtils.put("id", et_login_id.getText().toString());
                    SPUtils.put("password", et_login_password.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, MessageListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    DialogUtils.OneButtonDialog(LoginActivity.this, bean.getMsg());
                    getCodeImage();
                }
            }
        });
    }

    private void getCodeImage() {
        uuid = UUIDUtils.getUUID(8);
        String baseUrl;
        String nativeUrl = SPUtils.getString("url", "");
        if (TextUtils.isEmpty(nativeUrl)) {
            baseUrl = Util.ws;
        } else {
            baseUrl = nativeUrl;
        }
        Glide.with(LoginActivity.this).load(baseUrl + "api/common/getVerifiCode?uuid=" + uuid).into(iv_login_verification_code);
    }

}
