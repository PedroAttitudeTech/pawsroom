package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.model.SocketState;

import java.util.Arrays;

import io.reactivex.ObservableEmitter;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class OnDisconnectListener implements Emitter.Listener {

    public final static String TAG = "OnDisconnectListener";
    private final Socket socket;
    private final ObservableEmitter<SocketState> emitter;

    public OnDisconnectListener(Socket socket, ObservableEmitter<SocketState> emitter) {
        this.socket = socket;
        this.emitter = emitter;
    }

    @Override
    public void call(Object... args) {
        Log.e(TAG, "Disconnect " + Arrays.toString(args));
        emitter.onNext(SocketState.DISCONNECTED);
    }
}