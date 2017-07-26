package com.attitudetech.pawsroom.petList;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.attitudetech.pawsroom.R;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;
import com.attitudetech.pawsroom.socketio.SocketIOAllPetObserver;
import com.attitudetech.pawsroom.util.RxUtil;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

public class PetListActivity extends LifecycleActivity {

    PetListViewModel mViewModel;
    protected SocketIOAllPetObserver socketIoObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        mViewModel.flowablePetList
                .compose(RxUtil.applyFlowableSchedulers())
                .subscribe(new FlowableSubscriber<List<PetEntity>>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {

            }

            @Override
            public void onNext(List<PetEntity> petEntityList) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });


        socketIoObserver = new SocketIOAllPetObserver(this, this.getClass().getName());
        getLifecycle().addObserver(socketIoObserver);

        ((Button)findViewById(R.id.button2)).setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), PetListActivity2.class));
        });

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