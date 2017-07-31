package com.attitudetech.pawsroom.socketio;


import com.attitudetech.pawsroom.socketio.listener.OnAuthenticationListener;
import com.attitudetech.pawsroom.socketio.listener.OnConnectListener;
import com.attitudetech.pawsroom.socketio.listener.OnDisconnectListener;
import com.attitudetech.pawsroom.socketio.listener.SocketListener;
import com.attitudetech.pawsroom.socketio.model.SocketState;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.ReplaySubject;
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
    private Observable<SocketState> connectObservable;
    private static SocketManager instance;

    private SocketManager() {
        try {
            socket = IO.socket(SERVER_URL);
            listeners = new HashMap<>();
            connectObservable = BehaviorSubject.create(emitter -> {

                OnConnectListener onConnectListener = new OnConnectListener(socket);
                OnAuthenticationListener authenticationListener = new OnAuthenticationListener(emitter);
                OnDisconnectListener onDisconnectListener = new OnDisconnectListener(socket);

                socket.on(Socket.EVENT_CONNECT, onConnectListener);
                socket.on(AUTH_CHECK, authenticationListener);
                socket.on(Socket.EVENT_DISCONNECT, onDisconnectListener);

                if (socket.connected()) {
                    emitter.onNext(SocketState.AUTHENTICATED);
                } else {
                    socket.connect();
                }

                emitter.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {
                        socket
                                .off(Socket.EVENT_CONNECT, onConnectListener)
                                .off(AUTH_CHECK, authenticationListener);
                    }
                });
            });
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
        return connectObservable;
    }

    void emit(String event, Object... args) {
        socket.emit(event, args);
    }

    public <T> Flowable<T> on(final String channel, final SocketListener<T> listener) {
        if(listeners.containsKey(channel)){
            return listeners.get(channel);
        }
        Flowable<T> flowable =
                BehaviorProcessor.<T>create(emitter -> {
                    listener.setEmitter(emitter);

                    socket.on(channel, listener);
                    socket.connect();
                }, BackpressureStrategy.LATEST).serialize();

        listeners.put(channel, flowable);
        return flowable;
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