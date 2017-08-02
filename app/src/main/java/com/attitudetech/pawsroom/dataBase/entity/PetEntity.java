package com.attitudetech.pawsroom.dataBase.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;

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

    public PetEntity() {
    }

    @Ignore
    public PetEntity(String id) {
        this.id = id;
    }

    @Ignore
    public PetEntity(SocketIoPetInfo socketIoPetInfo) {
        this.id = socketIoPetInfo.id;
    }
}
