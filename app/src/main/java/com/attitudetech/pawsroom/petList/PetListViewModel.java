package com.attitudetech.pawsroom.petList;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.repository.PetRepository;
import com.attitudetech.pawsroom.socketio.SocketIOService;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by phrc on 7/19/17.
 */

public class PetListViewModel extends AndroidViewModel {

    protected LiveData<List<PetEntity>> observablePetList;

    private PetRepository petRepository;



    public PetListViewModel(Application application) {
        super(application);
        petRepository = new PetRepository();
        observablePetList = petRepository.getPetList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
