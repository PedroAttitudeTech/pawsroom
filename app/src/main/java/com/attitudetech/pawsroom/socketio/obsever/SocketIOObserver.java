package com.attitudetech.pawsroom.socketio.obsever;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.attitudetech.pawsroom.repository.PetRepository;

public abstract class SocketIOObserver implements LifecycleObserver {


    protected PetRepository petRepository;

    public SocketIOObserver() {
        petRepository = new PetRepository();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){
        Log.e("SocketIO", "Start ");
        startListeners();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(){
        Log.e("SocketIO", "Stop ");
//        if (!context.isChangingConfigurations()){
//            removeListeners();
//            Log.e("SocketIO", "configs change false");
//        }
        dispose();
    }

    protected abstract void startListeners();

    protected abstract void removeListeners();

    protected abstract void dispose();
}
