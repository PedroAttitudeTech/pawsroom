package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.model.SocketIOEvent;
import com.google.gson.Gson;

public class OnSocketIOEventListener extends SocketListener<SocketIOEvent> {

    public static final String TAG = "OnSocketIOEventListener";

    @Override
    public void call(Object... args) {
        SocketIOEvent socketIOEvent = new Gson().fromJson(args[0].toString(), SocketIOEvent.class);
        Log.d(TAG, socketIOEvent.toString());
        emitter.onNext(socketIOEvent);
    }
}