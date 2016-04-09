package com.sam_chordas.android.stockhawk.retrofit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by sanju singh on 4/8/2016.
 */
public class RestClient {
    private static final String BASE_URL = "https://query.yahooapis.com/v1/";
    private static ApiService apiService;

    private RestClient(){}

    static{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService()
    {
        return apiService;
    }
}

