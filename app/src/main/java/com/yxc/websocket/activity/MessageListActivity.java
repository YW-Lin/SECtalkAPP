package com.yxc.websocket.activity;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yxc.websocket.R;
import com.yxc.websocket.adapter.MessageListAdapter;
import com.yxc.websocket.im.JWebSocketClient;
import com.yxc.websocket.im.JWebSocketClientService;
import com.yxc.websocket.modle.LoginBean;
import com.yxc.websocket.modle.MessageListBean;
import com.yxc.websocket.modle.MessageResut;
import com.yxc.websocket.modle.PdataBean;
import com.yxc.websocket.modle.ReceiveMessageData;
import com.yxc.websocket.modle.request.UpdateSessionRequest;
import com.yxc.websocket.util.DialogUtils;
import com.yxc.websocket.util.HttpUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import okhttp3.Call;

public class MessageListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rv_message_list;
    private TextView tv_message_list_logout;
    private TextView message_list_setting;
    private MessageListAdapter adapter;
    private JWebSocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private ChatMessageReceiver chatMessageReceiver;
    private SmartRefreshLayout srl_message_refresh;
    private int code;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private MessageListBean messageListBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        getSupportActionBar().hide();
        initView();
        startJWebSClientService();
        bindService();
        doRegisterReceiver();
        checkNotification(this);

        getMessageList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        code = -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.message_list_setting:
//                Intent intent = new Intent(MessageListActivity.this, SettingActivity.class);
//                startActivity(intent);
//                break;
            case R.id.tv_message_list_logout:
                DialogUtils.TwoButtonDialog(MessageListActivity.this, "Whether or not to withdraw from?", new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        logout();
                    }
                });
                break;
        }
    }

    private void logout() {
        String token = SPUtils.getString("token", "");
        UpdateSessionRequest request = new UpdateSessionRequest(0, token);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/ws/offline", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(MessageListActivity.this, e.getMessage().toString());
            }

            @Override
            public void onResponse(String response, int id) {
                LoginBean bean = JSON.parseObject(response, new TypeReference<LoginBean>() {
                });
                if ("200".equals(bean.getCode())) {
                    goToLogin("successful exit!");
                } else if ("100".equals(bean.getCode())) {
                    goToLogin("Someone logged into your account!");
                } else {
                    DialogUtils.OneButtonDialog(MessageListActivity.this, bean.getMsg());
                }
            }
        });
    }

    private void goToLogin(String text) {
        DialogUtils.OneButtonDialog(MessageListActivity.this, text, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                Intent intent = new Intent(MessageListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            PdataBean bean = JSON.parseObject(message, new TypeReference<PdataBean>() {
            });
            String json = SignUtils.decodeToJson(bean.getPdata(), bean.getPkey());
            ReceiveMessageData resultBean = JSON.parseObject(json, new TypeReference<ReceiveMessageData>() {
            });
            if ("310".equals(resultBean.getCode())) {
                goToLogin("Someone logged into your account!");
                return;
            }
            for (MessageListBean.DataBean dataBean : messageListBean.getData()) {
                if (resultBean.getSendCode() == dataBean.getCode() && code != resultBean.getSendCode()) {
                    dataBean.setNew(true);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void startJWebSClientService() {
        Intent intent = new Intent(this, JWebSocketClientService.class);
        startService(intent);
    }

    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
    }

    private void getMessageList() {
        String token = SPUtils.getString("token", "");
        UpdateSessionRequest request = new UpdateSessionRequest(0, token);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/ws/getLineList", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(MessageListActivity.this, e.getMessage().toString());
            }

            @Override
            public void onResponse(String response, int id) {
                MessageResut bean = JSON.parseObject(response, new TypeReference<MessageResut>() {
                });
                String json = SignUtils.decodeToJson(bean.getPdata(), bean.getPkey());
                Log.e("TAG", json);
                messageListBean = JSON.parseObject(json, new TypeReference<MessageListBean>() {
                });
                if ("100".equals(messageListBean.getCode())) {
                    goToLogin("Someone logged into your account!");
                    return;
                }
                adapter.setNewData(messageListBean.getData());
                if (srl_message_refresh.isRefreshing()) {
                    srl_message_refresh.finishRefresh();
                }
            }
        });
    }

    private void initView() {
        rv_message_list = (RecyclerView) findViewById(R.id.rv_message_list);
        tv_message_list_logout = (TextView) findViewById(R.id.tv_message_list_logout);
//        message_list_setting = (TextView) findViewById(R.id.message_list_setting);
        srl_message_refresh = (SmartRefreshLayout) findViewById(R.id.srl_message_refresh);
        tv_message_list_logout.setOnClickListener(this);
//        message_list_setting.setOnClickListener(this);

        rv_message_list.setLayoutManager(new LinearLayoutManager(this));
        rv_message_list.setHasFixedSize(true);
        adapter = new MessageListAdapter(this);
        rv_message_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MessageListBean.DataBean bean = (MessageListBean.DataBean) adapter.getData().get(position);
                bean.setNew(false);
                adapter.notifyDataSetChanged();
                code = bean.getCode();
                Intent intent = new Intent(MessageListActivity.this, MainActivity.class);
                intent.putExtra("code", bean.getCode());
                intent.putExtra("name", bean.getUserName());
                startActivity(intent);
            }
        });
        srl_message_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getMessageList();
            }
        });
    }

    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("WARM PROMPT")
                    .setMessage("You have not turned on the system notification, which will affect the reception of messages. Do you want to turn it on?")
                    .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
