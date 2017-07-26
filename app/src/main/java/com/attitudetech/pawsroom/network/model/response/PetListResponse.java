package com.attitudetech.pawsroom.network.model.response;

import com.attitudetech.pawsroom.dataBase.entity.PetEntity;

import java.util.List;

/**
 * Created by phrc on 7/19/17.
 */

public class PetListResponse {

    public List<PetEntity> pets;

    public PetListResponse() {
    }

    public PetListResponse(List<PetEntity> pets) {
        this.pets = pets;
    }

}
