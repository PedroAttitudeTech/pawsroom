package com.attitudetech.pawsroom.socketio;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.util.Log;

import com.attitudetech.pawsroom.repository.PetRepository;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.obsever.SocketIOObserver;
import com.attitudetech.pawsroom.util.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by phrc on 7/24/17.
 */

public class SocketIOAllPetObserver extends SocketIOObserver {

    PetRepository petRepository;

    CompositeDisposable compositeDisposable;

    public SocketIOAllPetObserver(Activity context, String clientName) {
        super(context, clientName);
        compositeDisposable = new CompositeDisposable();
        petRepository = new PetRepository();
    }

    @Override
    protected void startListeners(){
        Log.e("SocketIO", clientName);

        compositeDisposable.add(
                new PetRepository()
                        .getAllPetId()
                        .flatMap(strings -> {
                            Log.e("SocketIO", "Go to start listen " + strings.toString());
                            return SocketIOService.getInstance().startListenSocketIO(clientName, strings);
                        })
                        .compose(RxUtil.applyFlowableSchedulers())
                        .subscribe(socketIoPetInfo -> {
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
        compositeDisposable.clear();
    }
}
