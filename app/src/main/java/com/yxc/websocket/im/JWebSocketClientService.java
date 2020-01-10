package com.yxc.websocket.im;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yxc.websocket.R;
import com.yxc.websocket.activity.MainActivity;
import com.yxc.websocket.modle.LoginBean;
import com.yxc.websocket.modle.MessageResultBean;
import com.yxc.websocket.modle.PdataBean;
import com.yxc.websocket.modle.request.SendSessionRequest;
import com.yxc.websocket.util.HttpUtils;
import com.yxc.websocket.util.SPUtils;
import com.yxc.websocket.util.SignUtils;
import com.yxc.websocket.util.Util;
import com.zhy.http.okhttp.callback.StringCallback;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import okhttp3.Call;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class JWebSocketClientService extends Service {
    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    private final static int GRAY_SERVICE_ID = 1001;

    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocketClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25) {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        acquireWakeLock();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    public JWebSocketClientService() {
    }


    private void initSocketClient() {
        String baseUrl;
        String nativeUrl = SPUtils.getString("url", "");
        if (TextUtils.isEmpty(nativeUrl)) {
            baseUrl = Util.ws;
        } else {
            baseUrl = nativeUrl;
        }
        URI uri = URI.create(baseUrl + "ws/asset");
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                PdataBean bean = JSON.parseObject(message, new TypeReference<PdataBean>() {
                });
                String json = SignUtils.decodeToJson(bean.getPdata(), bean.getPkey());
                MessageResultBean resultBean = JSON.parseObject(json, new TypeReference<MessageResultBean>() {
                });
                if (2 == resultBean.getMsgType()) {
                    if (310 == resultBean.getCode()) {
                        Intent intent = new Intent();
                        intent.setAction("com.xch.servicecallback.content");
                        intent.putExtra("message", message);
                        sendBroadcast(intent);
                    } else {
                        sendSessionId(resultBean);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction("com.xch.servicecallback.content");
                    intent.putExtra("message", message);
                    sendBroadcast(intent);

                    checkLockAndShowNotification("You have a new message");
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
            }
        };
        connect();
    }

    private void sendSessionId(MessageResultBean resultBean) {
        SendSessionRequest data = new SendSessionRequest(SPUtils.getString("token", ""), resultBean.getSessionId());
        String pdata = SignUtils.signPdata(data);
        String pkey = SignUtils.signPkey();
        HttpUtils.post("/api/ws/update4Session", pdata, pkey, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                LoginBean bean = JSON.parseObject(response, new TypeReference<LoginBean>() {
                });
                if ("200".equals(bean.getCode())) {
                }
            }
        });
    }

    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void sendMsg(String msg) {
        if (null != client) {
            client.send(msg);
        }
    }

    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    private void checkLockAndShowNotification(String content) {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();
                wl.release();
            }
            sendNotification(content);
        } else {
            sendNotification(content);
        }
    }

    private void sendNotification(String content) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("You have a new message")
                .setContentText(content)
                .setVisibility(VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();
        notifyManager.notify(1, notification);
    }


    private static final long HEART_BEAT_RATE = 10 * 1000;
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }
            } else {
                client = null;
                initSocketClient();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
