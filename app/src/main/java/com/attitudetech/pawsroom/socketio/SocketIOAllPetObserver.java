package com.attitudetech.pawsroom.socketio;

import android.app.Activity;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.attitudetech.pawsroom.socketio.obsever.SocketIOObserver;
import com.attitudetech.pawsroom.util.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

public class SocketIOAllPetObserver extends SocketIOObserver {

    private static final String TAG = "SocketIOAllPetObserver";
    private CompositeDisposable compositeDisposable;

    public SocketIOAllPetObserver(Activity context) {
        super(context);
        compositeDisposable = new CompositeDisposable();
    }

    @WorkerThread
    @Override
    protected void startListeners(){
        Log.e("SocketIO", TAG);

        compositeDisposable.add(
                        SocketIOService.getInstance().startListenSocketIO()
                                .compose(RxUtil.applyFlowableSchedulers())
                                .subscribe(socketIoPetInfo -> {
                                    Log.e("SocketIO", "On Next");

                                }, throwable -> {
                                    Log.e("SocketIO", "On Error");

                                })
        );
    }

    @WorkerThread
    @Override
    protected void removeListeners(){
    }

    @Override
    protected void dispose(){
        Log.d(TAG, "dispose compositeDisposable");
        compositeDisposable.clear();
    }
}
