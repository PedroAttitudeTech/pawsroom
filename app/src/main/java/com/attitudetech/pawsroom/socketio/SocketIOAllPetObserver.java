package com.attitudetech.pawsroom.socketio;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.support.annotation.BinderThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.repository.PetRepository;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.obsever.SocketIOObserver;
import com.attitudetech.pawsroom.util.RxUtil;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by phrc on 7/24/17.
 */

public class SocketIOAllPetObserver extends SocketIOObserver {



    CompositeDisposable compositeDisposable;

    public SocketIOAllPetObserver(Activity context, String clientName) {
        super(context, clientName);
        compositeDisposable = new CompositeDisposable();
    }

    @WorkerThread
    @Override
    protected void startListeners(){
        Log.e("SocketIO", clientName);

        compositeDisposable.add(
                petRepository
                        .getAllPetId()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.computation())
                        .subscribe(strings -> {
                            Log.e("SocketIO", "Go to start listen " + strings.toString());
                        })
//                        .flatMap(strings -> {
//                            Log.e("SocketIO", "Go to start listen " + strings.toString());
//                            return Flowable.just(new SocketIoPetInfo());
////                            return SocketIOService.getInstance().startListenSocketIO(clientName, strings);
//                        })
//                        .subscribeOn(Schedulers.computation())
//                        .observeOn(Schedulers.computation())
//                        .subscribe(socketIoPetInfo -> {
//                            updateDatabase(socketIoPetInfo);
//                            Log.e("SocketIO", "On Next");
//
//                        }, throwable -> {
//
//                            Log.e("SocketIO", "On Error "+throwable.getMessage());
//
//                        })
        );
    }

    @WorkerThread
    @Override
    protected void removeListeners(){
        SocketIOService
                .getInstance()
                .stopListenSocketIO(clientName)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(() -> {});
    }

    @Override
    protected void dispose(){
        compositeDisposable.clear();
    }
}
