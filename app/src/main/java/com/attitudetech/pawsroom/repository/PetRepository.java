package com.attitudetech.pawsroom.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.attitudetech.pawsroom.dataBase.DataBaseCreator;
import com.attitudetech.pawsroom.dataBase.dao.PetDao;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.network.ApiClient;
import com.attitudetech.pawsroom.network.requester.PetApiRequester;
import com.attitudetech.pawsroom.util.RxUtil;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
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
        petApiRequester
                .getPets()
                .flatMapCompletable(petListResponse ->
                        Completable.fromAction(
                                () ->  petDao.insertAll(petListResponse.pets)))
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
        return petDao.getAllLiveData();
    }


    public Flowable<List<String>> getAllPetId(){
        return petDao.getAllPetId();
    }

}
