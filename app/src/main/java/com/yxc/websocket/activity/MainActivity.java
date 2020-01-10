package com.yxc.websocket.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.flyco.dialog.listener.OnBtnClickL;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yxc.websocket.R;
import com.yxc.websocket.adapter.Adapter_ChatMessage;
import com.yxc.websocket.im.JWebSocketClient;
import com.yxc.websocket.im.JWebSocketClientService;
import com.yxc.websocket.im.SendMessageRequest;
import com.yxc.websocket.modle.ChatMessage;
import com.yxc.websocket.modle.CheckBean;
import com.yxc.websocket.modle.MessageListData;
import com.yxc.websocket.modle.MessageListRequestBean;
import com.yxc.websocket.modle.MessageResut;
import com.yxc.websocket.modle.PdataBean;
import com.yxc.websocket.modle.ReceiveMessageData;
import com.yxc.websocket.util.DialogUtils;
import com.yxc.websocket.util.HttpUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private JWebSocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private EditText et_content;
    private ListView listView;
    private Button btn_send;
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private Adapter_ChatMessage adapter_chatMessage;
    private ChatMessageReceiver chatMessageReceiver;
    private ImageView iv_return;
    private ImageView btn_multimedia;
    private SmartRefreshLayout srl_main_refresh;
    private TextView tv_groupOrContactName;
    private ImageView btn_face;
    private int pageSize = 10;
    private int pageNo = 1;
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
    private int code;
    private String name;

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
                goToLogin();
                return;
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMsg(resultBean.getMsg());
            chatMessage.setSendCode(resultBean.getSendCode());
            chatMessage.setCreate(resultBean.getCreate());
            chatMessage.setSendName(resultBean.getSendName());
            chatMessage.setAcceptCode(resultBean.getAcceptCode());
            chatMessage.setAcceptName(resultBean.getAcceptName());
            chatMessage.setIsMeSend(code == resultBean.getAcceptCode() ? 1 : 0);
            chatMessageList.add(chatMessage);
            initChatMsgListView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        startJWebSClientService();
        bindService();
        doRegisterReceiver();
        findViewById();
        initData();
        initView();
        getMessageList(false);
    }

    private void getMessageList(final boolean isFirst) {
        if (!isFirst) {
            pageNo = 1;
        }
        String token = SPUtils.getString("token", "");
        MessageListRequestBean request = new MessageListRequestBean(token, code, pageSize, pageNo);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/ws/getMsgList", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(MainActivity.this, e.getMessage().toString());
                srl_main_refresh.finishRefresh();
            }

            @Override
            public void onResponse(String response, int id) {
                srl_main_refresh.finishRefresh();
                MessageResut bean = JSON.parseObject(response, new TypeReference<MessageResut>() {
                });

                String json = SignUtils.decodeToJson(bean.getPdata(), bean.getPkey());
                MessageListData messageListData = new MessageListData();
                MessageListData.ResultData messageListBean = JSON.parseObject(json, new TypeReference<MessageListData.ResultData>() {
                });
                if ("100".equals(messageListBean.getCode())) {
                    goToLogin();
                    return;
                }
                messageListData.setMsgType(bean.getMsgType());
                messageListData.setPageNo(bean.getPageNo());
                messageListData.setPageSize(bean.getPageSize());
                messageListData.setData(messageListBean);

                for (MessageListData.ResultData.DataBean.ListBean dataBean : messageListBean.getData().getList()) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMsg(dataBean.getMsg());
                    chatMessage.setCreate(dataBean.getCreate());
                    chatMessage.setAcceptCode(dataBean.getAcceptCode());
                    chatMessage.setSendCode(dataBean.getSendCode());
                    chatMessage.setIsMeSend(code == dataBean.getAcceptCode() ? 1 : 0);
                    chatMessageList.add(chatMessage);
                }
                initChatMsgListView();
            }
        });
    }

    private void goToLogin() {
        DialogUtils.OneButtonDialog(MainActivity.this, "Someone logged into your account!", new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData() {
        code = getIntent().getIntExtra("code", -1);
        name = getIntent().getStringExtra("name");
        tv_groupOrContactName.setText(name);
    }

    private void bindService() {
        Intent bindIntent = new Intent(mContext, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void startJWebSClientService() {
        Intent intent = new Intent(mContext, JWebSocketClientService.class);
        startService(intent);
    }

    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
    }


    private void findViewById() {
        listView = (ListView) findViewById(R.id.chatmsg_listView);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_content = (EditText) findViewById(R.id.et_content);
        iv_return = (ImageView) findViewById(R.id.iv_return);
        srl_main_refresh = (SmartRefreshLayout) findViewById(R.id.srl_main_refresh);
        tv_groupOrContactName = (TextView) findViewById(R.id.tv_groupOrContactName);
        btn_face = (ImageView) findViewById(R.id.btn_face);
        btn_multimedia = (ImageView) findViewById(R.id.btn_multimedia);
        btn_send.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        btn_face.setOnClickListener(this);
        btn_multimedia.setOnClickListener(this);
        srl_main_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo++;
                getMessageList(true);
            }
        });
    }

    private void initView() {
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_content.getText().toString().length() > 0) {
                    btn_send.setVisibility(View.VISIBLE);
                } else {
                    btn_send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String content = et_content.getText().toString();
                if (content.length() <= 0) {
                    DialogUtils.OneButtonDialog(MainActivity.this, "MESSAGE CAN NOT BE EMPTY!");
                    return;
                }

                if (client != null && client.isOpen()) {
                    sendMsgToService(content);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMsg(content);
                    chatMessage.setAcceptCode(code);
                    chatMessage.setIsMeSend(1);
                    chatMessage.setCreate(System.currentTimeMillis());
                    chatMessageList.add(chatMessage);
                    initChatMsgListView();
                    et_content.setText("");
                } else {
                    DialogUtils.OneButtonDialog(MainActivity.this, "Connection disconnected，Please wait or restart the App!");
                }
                break;
            case R.id.iv_return:
                finish();
                break;
            case R.id.btn_face:
                DialogUtils.OneButtonDialog(MainActivity.this, "Coming soon!");
                break;
            case R.id.btn_multimedia:
                DialogUtils.OneButtonDialog(MainActivity.this, "Coming soon!");
                break;
            default:
                break;
        }
    }

    private void sendMsgToService(String content) {
        String token = SPUtils.getString("token", "");
        SendMessageRequest request = new SendMessageRequest(code, token, content);
        String pdata = SignUtils.signPdata(request);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("api/ws/sendOne", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                DialogUtils.OneButtonDialog(MainActivity.this, e.getMessage().toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", response);
                CheckBean messageListBean = JSON.parseObject(response, new TypeReference<CheckBean>() {
                });
                if ("100".equals(messageListBean.getCode())) {
                    goToLogin();
                }
            }
        });
    }

    private void initChatMsgListView() {
        adapter_chatMessage = new Adapter_ChatMessage(mContext, chatMessageList);
        listView.setAdapter(adapter_chatMessage);
        listView.setSelection(chatMessageList.size());
    }
}
