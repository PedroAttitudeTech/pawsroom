package com.attitudetech.pawsroom.socketio;

import android.support.annotation.NonNull;
import android.util.Log;

import com.attitudetech.pawsroom.socketio.listener.OnAuthenticationListener;
import com.attitudetech.pawsroom.socketio.listener.OnConnectListener;
import com.attitudetech.pawsroom.socketio.listener.OnDisconnectListener;
import com.attitudetech.pawsroom.socketio.listener.OnGpsDataReceivedListener;
import com.attitudetech.pawsroom.socketio.listener.SocketListener;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.model.SocketState;
import com.jakewharton.rx.ReplayingShare;

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

public class SocketManager {

    private static final String TAG = "SocketManager";

    static final String GPS_UPDATES = "gpsUpdate";
    public static final String AUTH_CHECK = "authCheck";
    static final String EVENTS = "events";
    static final String ROOMJOIN = "roomjoin";

    private static final String SERVER_URL = "http://eu.pawtrails.pet:2003";
    private static final String SERVER_URL_ssl = "https://eu.pawtrails.pet:2004";

    private Socket socket;
    private static SocketManager instance;
    private Observable<SocketState> connectObservable;
    private ConcurrentMap<String, Flowable<SocketIoPetInfo>> listeners;

    private SocketManager() {
        try {
            socket = IO.socket(SERVER_URL);
            listeners = new ConcurrentHashMap<>();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static SocketManager instance() {
        if(instance == null) {
            instance = new SocketManager();

        }
        return instance;
    }

    Observable<SocketState> connect() {
        if (connectObservable == null) {
            connectObservable = Observable
                    .<SocketState>create(emitter -> {
//                        socket.connect();TODO remove this. Check to see if this could cause issues
//                        socket.disconnect();

                        emitter.onNext(SocketState.INITIAL);
                        Log.d(TAG, "authenticate with server");
                        OnConnectListener onConnectListener = new OnConnectListener(socket, emitter);
                        OnAuthenticationListener authenticationListener = new OnAuthenticationListener(emitter);
                        OnDisconnectListener onDisconnectListener = new OnDisconnectListener(socket, emitter);
                        socket
                                .on(Socket.EVENT_CONNECT, onConnectListener)
                                .on(AUTH_CHECK, authenticationListener)
                                .on(Socket.EVENT_DISCONNECT, onDisconnectListener);

                        socket.connect();

                        emitter.setDisposable(new MainThreadDisposable() {
                            @Override
                            protected void onDispose() {
                                Log.d(TAG, "dispose Connection Listeners");
                                socket
                                        .off(Socket.EVENT_CONNECT, onConnectListener)
                                        .off(AUTH_CHECK, authenticationListener)
                                        .off(Socket.EVENT_DISCONNECT, onDisconnectListener);
                                connectObservable = null;
                            }
                        });
                    })
                    .compose(ReplayingShare.instance());
        }
        return connectObservable;
    }

    void emit(String event, Object... args) {
        Log.d(TAG, "emit " +  event + " for " + Arrays.toString(args));
        socket.emit(event, args);
    }

    <T> Flowable<T> on(final String channel, final SocketListener<T> listener) {
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
                            listeners.remove(channel);
                        }
                    });
                }, BackpressureStrategy.BUFFER);
    }

    Flowable<SocketIoPetInfo> on(final @NonNull String petId) {

        String channel = SocketManager.GPS_UPDATES + petId;
        if  (listeners.containsKey(channel)) {
            return listeners.get(channel);
        }
        emit(SocketManager.ROOMJOIN, petId);
        Flowable<SocketIoPetInfo> socketIoPetInfoFlowable = Flowable
                .<SocketIoPetInfo>create(emitter -> {
                    final OnGpsDataReceivedListener listener = new OnGpsDataReceivedListener();
                    listener.setEmitter(emitter);
                    Log.d(TAG, "connect channel " + channel);

                    socket.on(channel, listener);
                    socket.connect();

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            // We could socket.emit( leave room) here!!!
                            // But it's not mandatory since the server is handling it
                            Log.d(TAG, "dispose " + channel);
                            off(channel, listener);
                            listeners.remove(channel);
                        }
                    });
                }, BackpressureStrategy.BUFFER)
                .compose(ReplayingShare.instance());
        listeners.put(channel,  socketIoPetInfoFlowable);
        return socketIoPetInfoFlowable;
    }

//    public Observable<SocketState> disconnect() {
//        return Observable.create(e -> {
//            if (socket.connected()) {
//                OnDisconnectListener onDisconnectListener = new OnDisconnectListener(socket, e);
//
//                socket.on(Socket.EVENT_DISCONNECT, onDisconnectListener);
//                e.setDisposable(new MainThreadDisposable() {
//                    @Override
//                    protected void onDispose() {
//                        socket.off(Socket.EVENT_DISCONNECT, onDisconnectListener);
//                    }
//                });
//            } else {
//                e.onNext(SocketState.DISCONNECTED);
//            }
//        });
//    }

    private <T> void off(String channel, SocketListener<T> listener) {
        listeners.remove(channel);
        socket.off(channel, listener);
        if(listeners.isEmpty()) {
            socket.disconnect();
        }
    }
}