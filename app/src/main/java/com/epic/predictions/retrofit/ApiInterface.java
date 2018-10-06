package com.epic.predictions.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Abhilash on 17-09-2017
 */

public interface ApiInterface {

    //Previous Games
    @Headers("Content-type:application/json")
    @GET("/phoneprevious_json.php")
    Call<GameResponse> getPhonePreviousGames();

    //Today's Games
    @Headers("Content-type:application/json")
    @GET("/phonetoday_json.php")
    Call<GameResponse> getPhoneTodayGames();

}
