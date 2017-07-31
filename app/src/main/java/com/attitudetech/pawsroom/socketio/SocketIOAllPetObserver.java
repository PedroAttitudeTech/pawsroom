package com.attitudetech.pawsroom.socketio;

import android.app.Activity;
import android.util.Log;

import com.attitudetech.pawsroom.repository.PetRepository;
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
        compositeDisposable.add(
                petRepository
                        .getAllPetId()
                        .flatMap(strings -> SocketIOService.getInstance().startListenSocketIO(clientName, strings))
                        .compose(RxUtil.applyFlowableSchedulers())
                        .subscribe(r -> {
                            Log.e("SocketIO", "SocketUpdate "+r.id);
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
