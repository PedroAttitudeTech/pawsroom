package com.attitudetech.pawsroom.dataBase.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by phrc on 7/19/17.
 */

@Dao
public interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PetEntity> petEntities);

    @Query("SELECT * FROM pets")
    LiveData<List<PetEntity>> getAllLiveData();

    @Query("SELECT id FROM pets")
    Flowable<List<String>> getAllPetId();

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insert(PetEntity petEntity);

    @Query("DELETE from pets where id not in (:petIds)")
    void delete(List<String> petIds);
}
