package com.attitudetech.pawsroom.petList;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.attitudetech.pawsroom.R;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.socketio.SocketIOAllPetObserver;

public class PetListActivity2 extends LifecycleActivity {

    PetListViewModel2 mViewModel;
    protected SocketIOAllPetObserver socketIoObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);

        mViewModel = ViewModelProviders
                .of(this)
                .get(PetListViewModel2.class);

        mViewModel
                .observablePetList
                .observe(this, petEntities -> {
                    for (PetEntity petEntity : petEntities){
                        Log.e("test", petEntity.id);
                    }
                });


        findViewById(R.id.button2).setOnClickListener(v -> startActivity(new Intent(getBaseContext(), PetListActivity.class)));


        socketIoObserver = new SocketIOAllPetObserver(this);

        getLifecycle().addObserver(socketIoObserver);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



}
