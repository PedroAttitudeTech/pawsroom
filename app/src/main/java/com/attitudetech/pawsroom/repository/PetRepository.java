package com.attitudetech.pawsroom.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.DataBaseCreator;
import com.attitudetech.pawsroom.dataBase.dao.PetDao;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.network.ApiClient;
import com.attitudetech.pawsroom.network.requester.PetApiRequester;

import java.util.List;

import io.reactivex.Flowable;

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

//        petApiRequester
//                .getPets()
//                .flatMapCompletable(petListResponse ->
//                        Completable.fromAction(
//                                () ->  petDao.insertAll(petListResponse.pets)))
//                .compose(RxUtil.applyCompletableSchedulers())
//                .subscribe(new CompletableObserver() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//                });
        return petDao.getAllLiveData();
    }


    public Flowable<List<String>> getAllPetId(){
        Log.e("startListeners", "Get all pet ids");
        return petDao.getAllPetId();
    }

    public void insertOrUpdate(PetEntity pet){
        petDao.insert(pet);
    }

}
