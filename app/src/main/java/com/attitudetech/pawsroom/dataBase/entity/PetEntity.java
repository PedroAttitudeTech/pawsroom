package com.attitudetech.pawsroom.dataBase.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by phrc on 7/19/17.
 */

@Entity(tableName = "pets")
public class PetEntity {

    @PrimaryKey
    public String id;
    public String name;
    public String img_url;
    public boolean is_owner;

}
