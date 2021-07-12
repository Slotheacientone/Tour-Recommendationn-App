package edu.hcmuaf.tourrecommendationapp.filter;

import android.util.Log;

import edu.hcmuaf.tourrecommendationapp.dto.LoginResponse;
import edu.hcmuaf.tourrecommendationapp.service.AuthService;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private SharedPrefs sharedPrefs = SharedPrefs.getInstance();
    private AuthService authService = AuthService.getInstance();

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        //authenticate request
        if (originalRequest.url().toString().contains("/auth")) {
            return chain.proceed(originalRequest);
        }

        //send request
        Request authRequest;
        String token = Utils.getAccessToken();
        Log.i(TAG,"Send request with Token: " + token);
        authRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Response response = chain.proceed(authRequest);

        //handle expire token
        if (response.code() == 401) {
            try {
                synchronized (authService) {
                    String currentToken = Utils.getAccessToken();

                    //refresh token
                    if (currentToken != null && currentToken.equals(token)) {
                        int code = authService.refreshToken();

                        if (code >= 400) {
                            authService.logout();
                            return response;
                        }
                    }

                    response.close();

                    Log.i(TAG,"Resend request with new Token: " + Utils.getAccessToken());
                    authRequest = originalRequest.newBuilder()
                            .addHeader("Authorization", "Bearer " + Utils.getAccessToken())
                            .build();
                    return chain.proceed(authRequest);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
