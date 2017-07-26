package com.attitudetech.pawsroom.network.api;

import com.attitudetech.pawsroom.network.ApiClient;
import com.attitudetech.pawsroom.network.model.response.PetListResponse;
import com.attitudetech.pawsroom.network.requester.PetApiRequester;
import com.attitudetech.pawsroom.util.RxUtil;

import io.reactivex.Single;
import retrofit2.Retrofit;

public class PetApi implements IPetApi{

    private Retrofit retrofit;

    public PetApi() {
        retrofit = ApiClient.getRetrofitClient();

    }

    @Override
    public Single<PetListResponse> getPetList(){
        return retrofit
                .create(PetApiRequester.class)
                .getPets()
                .compose(RxUtil.applySingleSchedulers());
    }

}
