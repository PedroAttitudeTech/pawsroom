package com.attitudetech.pawsroom.socketio;


import com.attitudetech.pawsroom.socketio.listener.OnAuthenticationListener;
import com.attitudetech.pawsroom.socketio.listener.OnConnectListener;
import com.attitudetech.pawsroom.socketio.listener.SocketListener;
import com.attitudetech.pawsroom.socketio.model.SocketState;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    public static final String TAG = "SocketManager";

    static final String GPS_UDPATES = "gpsUpdate";
    public static final String AUTH_CHECK = "authCheck";
    static final String EVENTS = "events";
    static final String ROOMJOIN = "roomjoin";

    private static final String SERVER_URL = "http://eu.pawtrails.pet:2003";
    private static final String SERVER_URL_ssl = "https://eu.pawtrails.pet:2004";

    private Socket socket;
    private Map<String, Flowable> listeners;
    private boolean isAuth = false;

    private static SocketManager instance;

    private SocketManager() {
        try {
            socket = IO.socket(SERVER_URL);
            listeners = new HashMap<>();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static SocketManager instance() {
        if(instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    Observable<SocketState> connect() {
        return Observable.create(emitter -> {
            OnConnectListener onConnectListener = new OnConnectListener(socket);

            OnAuthenticationListener authenticationListener = new OnAuthenticationListener(emitter);
            socket
                    .on(Socket.EVENT_CONNECT, onConnectListener);
            socket
                    .on(AUTH_CHECK, authenticationListener);
            if (socket.connected()) {
                isAuth = true;
                emitter.onNext(SocketState.AUTHENTICATED);
            } else {
                socket.connect();
            }
            emitter.setDisposable(new MainThreadDisposable() {
                @Override
                protected void onDispose() {
                    isAuth = false;
                    socket
                            .off(Socket.EVENT_CONNECT, onConnectListener)
                            .off(AUTH_CHECK, authenticationListener);
                }
            });
        });
    }

    void emit(String event, Object... args) {
        socket.emit(event, args);
    }

    public <T> Flowable<T> on(final String channel, final SocketListener<T> listener) {
        if(listeners.containsKey(channel)){
            listeners.get(channel);
        }

        Flowable<T> flowable = ConnectableFlowable
                .create(emitter -> {
                    listener.setEmitter(emitter);

                    socket.on(channel, listener);
                    socket.connect();

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            SocketManager.instance().off(channel);
                        }
                    });
                }, BackpressureStrategy.BUFFER);



        listeners.put(channel, flowable);

        return flowable;
    }

    private <T> SocketManager off(String channel) {
        socket.off(channel);
        listeners.remove(channel);
        if(listeners.isEmpty()) {
            socket.disconnect();
            isAuth = false;
        }
        return this;
    }

    public boolean isAuth() {
        return isAuth;
    }
}