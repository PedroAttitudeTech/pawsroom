package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.SocketManager;
import com.attitudetech.pawsroom.socketio.model.SocketState;

import java.util.Arrays;

import io.reactivex.ObservableEmitter;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class OnConnectListener implements Emitter.Listener {

    public final static String TAG = "OnConnectListener";
    private final Socket socket;
    private ObservableEmitter<SocketState> emitter;

    public OnConnectListener(Socket socket, ObservableEmitter<SocketState> emitter) {
        this.socket = socket;
        this.emitter = emitter;
    }

    @Override
    public void call(Object... args) {
        Log.e(TAG, "onconnection " + Arrays.toString(args));
        emitter.onNext(SocketState.CONNECTED);
        socket.emit(SocketManager.AUTH_CHECK, "nrx6Jxc90IM9tGFlzz0cDckuJPAoaJBya8EUGQ4GzQQ2xvo64ojIADcksNIDwjbHsQB8GFPgwFj4bHbg5uYG7a0AYJeu2qcvgNDXtse08x4jffzkJx1RI2sH1503758930LGbN6ninbWkFpyGx5KQl0pGKXHCGJ4nuKziRXBf9xzPW7vtdfkzgKf0IWCpFHMasmsjj4ytB8iygN2u3n3j7ikQfXfUE146oxqIBZbd8tMohOSkbWEjeZ9uW");
    }
}
