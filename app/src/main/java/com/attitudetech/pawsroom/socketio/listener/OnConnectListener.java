package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import com.attitudetech.pawsroom.network.ApiClient;
import com.attitudetech.pawsroom.socketio.SocketManager;

import java.util.Arrays;

import io.socket.client.Socket;

public class OnConnectListener extends SocketListener<String> {

    public final static String TAG = "OnConnectListener";
    private final Socket socket;

    public OnConnectListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void call(Object... args) {
        Log.e(TAG, "onconnection " + Arrays.toString(args));
        socket.emit(SocketManager.AUTH_CHECK, "nrx6Jxc90IM9tGFlzz0cDckuJPAoaJBya8EUGQ4GzQQ2xvo64ojIADcksNIDwjbHsQB8GFPgwFj4bHbg5uYG7a0AYJeu2qcvgNDXtse08x4jffzkJx1RI2sH1503758930LGbN6ninbWkFpyGx5KQl0pGKXHCGJ4nuKziRXBf9xzPW7vtdfkzgKf0IWCpFHMasmsjj4ytB8iygN2u3n3j7ikQfXfUE146oxqIBZbd8tMohOSkbWEjeZ9uW");
    }
}
