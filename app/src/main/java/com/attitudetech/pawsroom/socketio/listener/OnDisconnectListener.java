package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import java.util.Arrays;

import io.socket.client.Socket;

public class OnDisconnectListener extends SocketListener<String> {

    public final static String TAG = "OnDisconnectListener";
    private final Socket socket;

    public OnDisconnectListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void call(Object... args) {
        Log.e(TAG, "onconnection " + Arrays.toString(args));
//        SocketManager.instance().clearInstance();
    }
}