package edu.hcmuaf.tourrecommendationapp.util;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.hcmuaf.tourrecommendationapp.filter.AuthInterceptor;
import lombok.extern.log4j.Log4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {
    private static final String TAG = "ApiClient";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final ApiClient mInstance = new ApiClient();

    private static OkHttpClient client;

    private ApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor())
                .build();
    }

    public static CompletableFuture<Response> sendAsync(Request request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Send request: " + request);
                return client.newCall(request).execute();
            } catch (IOException exception) {
                Log.e(TAG, exception.getMessage());
            }
            return null;
        }, executor);
    }

}
