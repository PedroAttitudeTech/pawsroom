package com.attitudetech.pawsroom.network.api;

import com.attitudetech.pawsroom.network.model.response.PetListResponse;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by phrc on 7/13/17.
 */

public interface IPetApi {

    Single<PetListResponse> getPetList();
}
