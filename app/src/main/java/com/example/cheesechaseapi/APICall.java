package com.example.cheesechaseapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APICall {
    public interface ObstacleLimitCallback{
        void onResponse(ObstacleLimit limit);
    }
    public interface ImageCallback {
        void onResponse(Bitmap bitmap);
    }
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://chasedeux.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create()).build();
    APIService service =retrofit.create(APIService.class);
    public void getObstacleLimit(Context context, final ObstacleLimitCallback callback){

        Call<ObstacleLimit> call = service.getObstacleLimit();

        call.enqueue(new Callback<ObstacleLimit>() {
            @Override
            public void onResponse(Call<ObstacleLimit> call, Response<ObstacleLimit> response) {
                if(response.isSuccessful()){
                    //If response is successful, parse the object into a Datamodel Object
                    ObstacleLimit obstacleLimit = response.body();

                    // Call the callback function with the DataModel
                    // object as a parameter.
                    callback.onResponse(obstacleLimit);
                }
            }
            @Override
            public void onFailure(Call<ObstacleLimit> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "Request failed",Toast.LENGTH_SHORT);
            }
        });
    }

    public void getImage(Context context, final ImageCallback callback, String character) {
        Call<ResponseBody> call = service.getImage(character);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    callback.onResponse(bitmap);
                } else {
                    Toast.makeText(context, "Failed to get image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Couldn't load bitmap/image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
