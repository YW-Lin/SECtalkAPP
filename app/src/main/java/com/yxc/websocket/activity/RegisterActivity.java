package com.yxc.websocket.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.yxc.websocket.R;
import com.yxc.websocket.modle.RegisterBean;
import com.yxc.websocket.modle.request.RegisterRequest;
import com.yxc.websocket.util.DialogUtils;
import com.yxc.websocket.util.HttpUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.yxc.websocket.util.UUIDUtils;
import com.yxc.websocket.util.Util;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_register_id;
    private EditText et_register_password;
    private EditText et_register_password_confirm;
    private EditText et_register_verification_code;
    private ImageView iv_register_verification_code;
    private Button btn_register_register;
    private LinearLayout ll_register_parent;
    private ImageView iv_register_return;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        initView();
        getCodeImage();
    }

    private void initView() {
        et_register_id = (EditText) findViewById(R.id.et_register_id);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_password_confirm = (EditText) findViewById(R.id.et_register_password_confirm);
        btn_register_register = (Button) findViewById(R.id.btn_register_register);
        et_register_verification_code = (EditText) findViewById(R.id.et_register_verification_code);
        iv_register_verification_code = (ImageView) findViewById(R.id.iv_register_verification_code);
        ll_register_parent = (LinearLayout) findViewById(R.id.ll_register_parent);
        iv_register_return = (ImageView) findViewById(R.id.iv_register_return);

        btn_register_register.setOnClickListener(this);
        ll_register_parent.setOnClickListener(this);
        iv_register_return.setOnClickListener(this);
        iv_register_verification_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_register:
                register();
                break;
            case R.id.ll_register_parent:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ll_register_parent.getWindowToken(), 0);
                break;
            case R.id.iv_register_return:
                finish();
                break;
            case R.id.iv_register_verification_code:
                getCodeImage();
                break;
        }
    }

    private void register() {
        if (TextUtils.isEmpty(et_register_id.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Please enter your ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_register_password.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_register_password_confirm.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Enter your password again", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!et_register_password.getText().toString().equals(et_register_password_confirm.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Please confirm that your password is the same as the confirmation password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (et_register_password.getText().toString().length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password must be greater than 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }
        submitRegister();
    }

    private void submitRegister() {
        RegisterRequest request = new RegisterRequest(et_register_id.getText().toString(), et_register_password.getText().toString(), "2", "2", et_register_verification_code.getText().toString(), uuid);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/ws/registerUser", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(RegisterActivity.this, e.getMessage().toString());
            }

            @Override
            public void onResponse(String response, int id) {
                RegisterBean bean = JSON.parseObject(response, new TypeReference<RegisterBean>() {
                });
                if("200".equals(bean.getCode())) {
                    DialogUtils.OneButtonDialog(RegisterActivity.this, bean.getMsg());
                    finish();
                }else {
                    DialogUtils.OneButtonDialog(RegisterActivity.this, bean.getMsg());
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
        Glide.with(RegisterActivity.this).load(baseUrl + "api/common/getVerifiCode?uuid=" + uuid).into(iv_register_verification_code);
    }
}
