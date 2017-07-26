package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.model.AuthenticationCheck;
import com.attitudetech.pawsroom.socketio.model.SocketState;
import com.google.gson.Gson;

import io.reactivex.ObservableEmitter;
import io.socket.emitter.Emitter;

public class OnAuthenticationListener implements Emitter.Listener {

    final static String TAG = "OnAuthenticationListene";

    private ObservableEmitter<SocketState> emitter;

    public OnAuthenticationListener(ObservableEmitter<SocketState> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void call(Object... args) {
        AuthenticationCheck authenticationCheck = new Gson().fromJson(args[0].toString(), AuthenticationCheck.class);
        Log.e(TAG, "auth check " + authenticationCheck.result + " error " + authenticationCheck.error);

        if (authenticationCheck.error > 0) {
            emitter.onError(new Throwable(String.valueOf(authenticationCheck.error)));
        } else {
            emitter.onNext(SocketState.getSocketStateFrom(authenticationCheck));
        }
    }
}
