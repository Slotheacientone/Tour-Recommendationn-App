package edu.hcmuaf.tourrecommendationapp.filter;

import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {


    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request authRequest = originalRequest;
        if (!originalRequest.url().toString().contains("/auth")) {
            authRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + Utils.getAccessToken())
                    .build();
        }
        Response response = chain.proceed(authRequest);
        return response;
    }

}
