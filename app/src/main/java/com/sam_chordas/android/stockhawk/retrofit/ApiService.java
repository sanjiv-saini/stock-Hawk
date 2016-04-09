package com.sam_chordas.android.stockhawk.retrofit;

import com.sam_chordas.android.stockhawk.retrofit.model.QuoteData;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sanju singh on 4/8/2016.
 */
public interface ApiService {

    @GET("public/yql")
    Call<QuoteData> getHistoryData(@Query("q") String q, @Query("diagnostics") String diagnostics,
                                   @Query("env") String env, @Query("format") String format);

}
