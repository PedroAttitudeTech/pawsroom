package com.attitudetech.pawsroom.socketio.listener;

import com.attitudetech.pawsroom.socketio.model.SocketIOEvent;
import com.google.gson.Gson;

public class OnSocketIOEventListener extends SocketListener<SocketIOEvent> {

    @Override
    public void call(Object... args) {
        SocketIOEvent socketIOEvent = new Gson().fromJson(args[0].toString(), SocketIOEvent.class);
        emitter.onNext(socketIOEvent);
    }
}
