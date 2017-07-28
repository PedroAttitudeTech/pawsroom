package com.attitudetech.pawsroom.socketio.listener;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.ObservableEmitter;
import io.socket.emitter.Emitter;

public abstract class SocketListener<T> implements Emitter.Listener {

    public FlowableEmitter<T> emitter;

    public void setEmitter(FlowableEmitter<T> emitter) {
        this.emitter = emitter;
    }

    public void complete(){
        emitter.onComplete();
    }
}
