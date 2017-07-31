package com.attitudetech.pawsroom.socketio;


import android.util.Log;

import com.attitudetech.pawsroom.socketio.listener.OnAuthenticationListener;
import com.attitudetech.pawsroom.socketio.listener.OnConnectListener;
import com.attitudetech.pawsroom.socketio.listener.SocketListener;
import com.attitudetech.pawsroom.socketio.model.SocketState;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    private static final String TAG = "SocketManager";

    static final String GPS_UPDATES = "gpsUpdate";
    public static final String AUTH_CHECK = "authCheck";
    static final String EVENTS = "events";
    static final String ROOMJOIN = "roomjoin";

    private static final String SERVER_URL = "http://eu.pawtrails.pet:2003";
    private static final String SERVER_URL_ssl = "https://eu.pawtrails.pet:2004";

    private Socket socket;
    private ConcurrentMap<String, SocketListener> listeners;
//    private AtomicBoolean isConnected;

    SocketManager() {
        try {
            socket = IO.socket(SERVER_URL);
            listeners = new ConcurrentHashMap<>();
//            isConnected = new AtomicBoolean();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    Observable<SocketState> connect() {
//        isConnected.set(true);
        return Observable.create(emitter -> {
            Log.d(TAG, "connect Observable");
            OnConnectListener onConnectListener = new OnConnectListener(socket);
            OnAuthenticationListener authenticationListener = new OnAuthenticationListener(emitter);

            //TODO add in listeners???
            socket
                    .on(Socket.EVENT_CONNECT, onConnectListener)
                    .on(AUTH_CHECK, authenticationListener)
                    .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "disconnect" + Arrays.toString(args));
                        }
                    })
                    .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "error " + Arrays.toString(args));

                        }
                    });
            if (socket.connected()) {
                Log.d(TAG, "connected -> on next Authentication");
                emitter.onNext(SocketState.AUTHENTICATED);
            } else {
                Log.d(TAG, "connect not connected");
                socket.connect();
            }
            emitter.setDisposable(new MainThreadDisposable() {
                @Override
                protected void onDispose() {
                    Log.d(TAG, "dispose connection listener");
                    socket
                            .off(Socket.EVENT_CONNECT, onConnectListener)
                            .off(AUTH_CHECK, authenticationListener)
                            .off(Socket.EVENT_DISCONNECT)
                            .off(Socket.EVENT_CONNECT_ERROR);
                }
            });
        });
    }

    void emit(String event, Object... args) {
        Log.d(TAG, "emit " +  event + " for " + Arrays.toString(args));
        socket.emit(event, args);
    }

    <T> Flowable<T> on(final String channel, final SocketListener<T> listener) {
        listeners.put(channel, listener);
        return Flowable
                .create(emitter -> {
                    listener.setEmitter(emitter);

                    socket.on(channel, listener);
                    socket.connect();

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            Log.d(TAG, "dispose " + channel);
                            off(channel, listener);
                        }
                    });
                }, BackpressureStrategy.BUFFER);
    }

    private <T> void off(String channel, SocketListener<T> listener) {
        listeners.remove(channel);
        socket.off(channel, listener);
        if(listeners.isEmpty()) {
//            isConnected.set(false);
            socket.disconnect();
        }
    }
}