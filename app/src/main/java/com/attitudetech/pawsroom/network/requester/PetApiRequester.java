package com.attitudetech.pawsroom.network.requester;

import com.attitudetech.pawsroom.network.model.response.PetListResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface PetApiRequester {

    @GET("pets/my/list")
    @Headers("token: WDFh61Sxn65NBEIVBMwssqbitZrQ1qKX4pfbq7INeNBQskM47iwzIIRcHj2JKMGOcVZD3Iqhv27XnT2uby3TgV5Yf8HZUoO6kOJnwaF1dMZBG15SA9MR5SPk15030486110xkVV81fWLCsWhua4uLLwRD61qY6jOrjlLegTgvP28iYqM8vhUgNLUTMlSTEGkX15ciYsOOvW7unUDSbx9Yj3S5oKZ2qj0sodKnGycbvjFTdiLoPUm8YeenY")
    Single<PetListResponse> getPets();

}
