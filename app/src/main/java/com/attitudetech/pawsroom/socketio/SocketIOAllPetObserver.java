package com.attitudetech.pawsroom.socketio;

import android.app.Activity;
import android.util.Log;

import com.attitudetech.pawsroom.socketio.obsever.SocketIOObserver;
import com.attitudetech.pawsroom.util.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

public class SocketIOAllPetObserver extends SocketIOObserver {

    private static final String TAG = "SocketIOAllPetObserver";
    CompositeDisposable compositeDisposable;

    public SocketIOAllPetObserver(Activity context, String clientName) {
        super(context, clientName);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void startListeners(){
        Log.e("SocketIO", clientName);

        compositeDisposable.add(
                petRepository
                        .getAllPetId()
                        .flatMap(strings -> {
                            Log.e("SocketIO", "Go to start listen " + strings.toString());
                            return SocketIOService.getInstance().startListenSocketIO(clientName, strings);
                        })
                        .compose(RxUtil.applyFlowableSchedulers())
                        .subscribe(socketIoPetInfo -> {
//                            updateDatabase(socketIoPetInfo);
                            Log.e("SocketIO", "On Next");

                        }, throwable -> {
                            Log.e("SocketIO", "On Error");

                        })
        );
    }

    @Override
    protected void removeListeners(){
//        SocketIOService
//                .getInstance()
//                .stopListenSocketIO(clientName)
//                .subscribe(() -> {});
    }

    @Override
    protected void dispose(){
        Log.d(TAG, "dispose compositeDisposable");
        compositeDisposable.clear();
    }
}
