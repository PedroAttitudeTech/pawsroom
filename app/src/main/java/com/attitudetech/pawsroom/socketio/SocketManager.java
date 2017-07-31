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
    private Map<String, Flowable> listeners;
    private Observable<SocketState> connectObservable;
    private static SocketManager instance;
    private ConcurrentMap<String, SocketListener> listeners;


    SocketManager() {
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

            if (socket.connected()) {
                emitter.onNext(SocketState.AUTHENTICATED);
            } else {

                OnConnectListener onConnectListener = new OnConnectListener(socket);
                OnAuthenticationListener authenticationListener = new OnAuthenticationListener(emitter);

                socket
                        .on(Socket.EVENT_CONNECT, onConnectListener);
                socket
                        .on(AUTH_CHECK, authenticationListener);

                socket.connect();

                emitter.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {
                        socket
                                .off(Socket.EVENT_CONNECT, onConnectListener)
                                .off(AUTH_CHECK, authenticationListener);
                    }
                });

            }


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

    public Completable off(String channel) {
        return Completable.fromAction(() -> {
            socket.off(channel);
            ((SocketListener)socket.listeners(channel).get(0)).complete();
            listeners.remove(channel);
            if(listeners.isEmpty()) {
                socket.disconnect();
            }
        });
    }

    public void clearInstance(){
        listeners.clear();
    }

}