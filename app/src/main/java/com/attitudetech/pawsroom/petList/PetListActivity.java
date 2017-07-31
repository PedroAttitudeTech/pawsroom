package com.attitudetech.pawsroom.petList;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.attitudetech.pawsroom.R;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.socketio.SocketIOAllPetObserver;

public class PetListActivity extends LifecycleActivity {

    PetListViewModel mViewModel;
    protected SocketIOAllPetObserver socketIoObserver;
//    static TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView = new TextView(this);
        mViewModel = ViewModelProviders
                .of(this)
                .get(PetListViewModel.class);

        mViewModel
                .observablePetList
                .observe(this, petEntities -> {
                    for (PetEntity petEntity : petEntities){
                        Log.e("test", petEntity.id);
                    }
                });

        socketIoObserver = new SocketIOAllPetObserver(this, this.getClass().getName());
        getLifecycle().addObserver(socketIoObserver);

        findViewById(R.id.button2).setOnClickListener(v -> startActivity(new Intent(getBaseContext(), PetListActivity2.class)));

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ConfigChange", isChangingConfigurations()+"");
    }
}
