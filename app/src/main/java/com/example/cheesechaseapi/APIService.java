package com.example.cheesechaseapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET("/obstacleLimit")
    Call<ObstacleLimit> getObstacleLimit();

    @GET("/image")
    Call<ResponseBody> getImage(@Query("character") String character);


}
