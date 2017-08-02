package com.attitudetech.pawsroom.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.DataBaseCreator;
import com.attitudetech.pawsroom.dataBase.dao.PetDao;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.network.ApiClient;
import com.attitudetech.pawsroom.network.requester.PetApiRequester;
import com.attitudetech.pawsroom.util.RxUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by phrc on 7/19/17.
 */

public class PetRepository {

    PetApiRequester petApiRequester;
    PetDao petDao;

    public PetRepository() {
        petApiRequester = ApiClient.getRetrofitClient().create(PetApiRequester.class);
        petDao = DataBaseCreator.getInstance().getDatabase().petDao();

    }

    public LiveData<List<PetEntity>> getPetList(){
        refreshPetList();
        return petDao.getAllLiveData();
    }

    public void refreshPetList(){
        petApiRequester
                .getPets()
                .flatMapCompletable(petListResponse ->
                        insertAll(petListResponse.pets)
                                .mergeWith(deleteAllExcept(petListResponse.pets)))
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


    public Flowable<List<String>> getAllPetId(){
        Log.e("startListeners", "Get all pet ids");
        return petDao.getAllPetId();
    }

    public void insertOrUpdate(PetEntity pet){
        petDao.insert(pet);
    }

    private Completable deleteAllExcept(List<PetEntity> petEntities){
        return Flowable.fromIterable(petEntities)
                .map(petEntity -> petEntity.id)
                .toList()
                .flatMapCompletable(s ->
                        Completable.fromAction(() -> petDao.delete(s)));
    }

    private Completable insertAll(List<PetEntity> petEntities){
        return Completable.fromAction(() -> petDao.insertAll(petEntities));
    }

}
