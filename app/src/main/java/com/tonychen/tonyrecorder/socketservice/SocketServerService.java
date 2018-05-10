package com.tonychen.tonyrecorder.socketservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerService extends Service {
    private static final String TAG = SocketServerService.class.getSimpleName();

    private ServerSocket mServerSocket;
    private HandlerThread mSocketServerThread;
    private Handler mHandler;
    /**
     * Socket服务端端口
     */
    private int mServerSocketPort = 4321;

    public SocketServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mSocketServerThread = new HandlerThread("SocketServerThread", Process.THREAD_PRIORITY_BACKGROUND);
        mSocketServerThread.start();
        mHandler = new Handler(mSocketServerThread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initSocketServer();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketServerThread != null) {
            mSocketServerThread.quitSafely();
        }
    }

    private void initSocketServer() {
        try {
            mServerSocket = new ServerSocket(mServerSocketPort);
            while (true) {
                Socket clientSocket = mServerSocket.accept();
                new ClientSocketHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientSocketHandler extends Thread {
        private Socket mClientSocket;
        private OutputStream mOutputStream;
        private InputStream mInputStream;

        public ClientSocketHandler(Socket socket) {
            super();
            this.mClientSocket = socket;
            try {
                mOutputStream = mClientSocket.getOutputStream();
                mInputStream = mClientSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
        }
    }
}
