package com.attitudetech.pawsroom.socketio;

import android.app.Activity;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.repository.PetRepository;
import com.attitudetech.pawsroom.socketio.obsever.SocketIOObserver;
import com.attitudetech.pawsroom.util.RxUtil;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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
//        compositeDisposable.add(
        petRepository
                .getPetListFlowable()
                .compose(RxUtil.applyFlowableSchedulers())
                .subscribe(new FlowableSubscriber<List<PetEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {

                    }

                    @Override
                    public void onNext(List<PetEntity> petEntityList) {
                        Log.e("SocketIO", "Response");

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
//        );
//                        .flatMapIterable(petEntityList -> petEntityList)
//                        .map(petEntity -> petEntity.id)
//                        .toList()
//                        .subscribe((strings, throwable) ->{
//                                Log.e("SocketIO", "Response");
//                                SocketIOService
//                                        .getInstance()
//                                        .startListenSocketIO(clientName, strings);}));
    }

    @Override
    protected void removeListeners(){
        Completable
                .fromAction(() ->  SocketIOService.getInstance().stopListenSocketIO(clientName))
                .compose(RxUtil.applyCompletableSchedulers())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    @Override
    protected void dispose(){
        compositeDisposable.dispose();
    }
}
