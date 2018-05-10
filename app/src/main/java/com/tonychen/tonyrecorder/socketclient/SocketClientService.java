package com.tonychen.tonyrecorder.socketclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.Socket;

public class SocketClientService extends Service {
    private static final String TAG = SocketClientService.class.getSimpleName();

    private Socket mSocket;

    public SocketClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSocketClient();
    }

    private void initSocketClient() {
        mSocket = new Socket()
    }
}
