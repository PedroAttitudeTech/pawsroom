/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.attitudetech.pawsroom.dataBase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.attitudetech.pawsroom.PawsRoomApplication;
import com.attitudetech.pawsroom.util.RxUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Creates the {@link AppDataBase} asynchronously, exposing a LiveData object to notify of creation.
 */
public class DataBaseCreator {

    private static DataBaseCreator sInstance;

    private AppDataBase mDb;

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public synchronized static DataBaseCreator getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DataBaseCreator();
                }
            }
        }
        return sInstance;
    }

    private DataBaseCreator() {
        mDb = Room.databaseBuilder(PawsRoomApplication.applicationContext.getApplicationContext(),
                AppDataBase.class, AppDataBase.DATABASE_NAME).build();
    }

    @Nullable
    public AppDataBase getDatabase() {
        return mDb;
    }

}