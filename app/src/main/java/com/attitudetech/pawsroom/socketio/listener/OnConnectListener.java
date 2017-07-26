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
        socket.emit(SocketManager.AUTH_CHECK, "WDFh61Sxn65NBEIVBMwssqbitZrQ1qKX4pfbq7INeNBQskM47iwzIIRcHj2JKMGOcVZD3Iqhv27XnT2uby3TgV5Yf8HZUoO6kOJnwaF1dMZBG15SA9MR5SPk15030486110xkVV81fWLCsWhua4uLLwRD61qY6jOrjlLegTgvP28iYqM8vhUgNLUTMlSTEGkX15ciYsOOvW7unUDSbx9Yj3S5oKZ2qj0sodKnGycbvjFTdiLoPUm8YeenY");
    }
}
