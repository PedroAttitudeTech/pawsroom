package com.attitudetech.pawsroom.dataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.attitudetech.pawsroom.dataBase.dao.PetDao;
import com.attitudetech.pawsroom.dataBase.entity.PetEntity;

@Database(entities = {PetEntity.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase{

    static final String DATABASE_NAME = "paws-trails-db";

    public abstract PetDao petDao();

}
