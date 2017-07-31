package com.attitudetech.pawsroom.socketio.obsever;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.socketio.SocketIOService;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by phrc on 7/25/17.
 */

public abstract class SocketIOObserver implements LifecycleObserver {

    protected Activity context;

    protected String clientName;


    public SocketIOObserver(Activity context, String clientName) {
        this.context = context;
        this.clientName = clientName;

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){
        Log.e("SocketIO", "Start ");
        startListeners();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(){
        Log.e("SocketIO", "Stop ");
        if (!context.isChangingConfigurations()){
            removeListeners();
            Log.e("SocketIO", "configs change false");
        }
        dispose();
    }

    protected abstract void startListeners();

    protected abstract void removeListeners();

    protected abstract void dispose();
}
