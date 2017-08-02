package com.attitudetech.pawsroom.socketio;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.attitudetech.pawsroom.socketio.obsever.SocketIOObserver;
import com.attitudetech.pawsroom.util.IOScheduler;
import com.attitudetech.pawsroom.util.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

public class SocketIOAllPetObserver extends SocketIOObserver {

    private static final String TAG = "SocketIOAllPetObserver";
    private CompositeDisposable compositeDisposable;

    public SocketIOAllPetObserver() {
        super();
        compositeDisposable = new CompositeDisposable();
    }

    @WorkerThread
    @Override
    protected void startListeners() {
        Log.e("SocketIO", TAG);

        compositeDisposable.add(
                SocketIOService.getInstance().startListenSocketIO()
                        .compose(RxUtil.applyFlowableSchedulers())
                        .subscribe(socketIoPetInfo -> {
                            Log.e("SocketIO", "On Next gps update");

                        }, throwable -> {
                            Log.e("SocketIO", "On Error");

                        })
        );
        compositeDisposable.add(
                SocketIOService
                        .getInstance()
                        .getSocketIOEventsFlowable()
                        .compose(IOScheduler.instance())
                        .subscribe(socketIOEvent -> {
                            Log.e("SocketIO", "On Next socket IO event");
                        })
        );
    }

    @WorkerThread
    @Override
    protected void removeListeners() {
    }

    @Override
    protected void dispose() {
        Log.d(TAG, "dispose compositeDisposable");
        compositeDisposable.clear();
    }
}
