package com.attitudetech.pawsroom.petList;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.repository.PetRepository;
import com.attitudetech.pawsroom.socketio.SocketIOService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

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
        ArrayList<PetEntity> pets = new ArrayList<>();
        pets.add(new PetEntity("93"));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void refreshList(){
        Log.e("LifeCycle-VM", "onStart");
        petRepository.refreshPetList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
