package edu.hcmuaf.tourrecommendationapp.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.LoginActivity;
import edu.hcmuaf.tourrecommendationapp.dto.LoginRequest;
import edu.hcmuaf.tourrecommendationapp.dto.LoginResponse;
import edu.hcmuaf.tourrecommendationapp.dto.RefreshTokenRequest;
import edu.hcmuaf.tourrecommendationapp.dto.RefreshTokenResponse;
import edu.hcmuaf.tourrecommendationapp.dto.RegisterRequest;
import edu.hcmuaf.tourrecommendationapp.model.ApiResponse;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.App;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {
    private static final String TAG = "AuthService";
    private static AuthService mInstance;
    private final UserService userService;
    private final SharedPrefs sharedPrefs;

    private AuthService() {
        userService = UserService.getInstance();
        sharedPrefs = SharedPrefs.getInstance();
    }

    public static AuthService getInstance() {
        if (mInstance == null) {
            mInstance = new AuthService();
        }
        return mInstance;
    }

    public boolean login(LoginRequest loginRequest) throws Exception {
        //validate
        if (!validate(loginRequest)) {
            return false;
        }

        //build request
        RequestBody requestBody = RequestBody.create(Utils.toJson(loginRequest), ApiClient.JSON);
        Request request = new Request.Builder()
                .url(Utils.BASE_URL + "/auth/login")
                .post(requestBody)
                .build();

        Response response = ApiClient.sendAsync(request).get();
        if (response == null) {
            throw new IOException("Send request fail: " + request);
        }

        if (!response.isSuccessful()) {
            return false;
        }

        //parse token
        LoginResponse loginResponse = Utils.fromJson(response.body().string(), LoginResponse.class);

        //save info
        SharedPrefs sharedPrefs = SharedPrefs.getInstance();
        sharedPrefs.put("auth", loginResponse);

        User user = userService.getInfo(loginRequest.getUsername());
        sharedPrefs.put("myInfo", user);


        return true;
    }

    private boolean validate(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        return (username != null && username.length() >= 6) && (password != null && password.length() >= 6);
    }

    private boolean validate(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String name = registerRequest.getName();
        return (username != null && username.length() >= 6)
                && (password != null && password.length() >= 6)
                && (name != null && name.length() >= 3);
    }

    public boolean register(RegisterRequest registerRequest) throws Exception {
        if (!validate(registerRequest))
            return false;

        //build request
        RequestBody requestBody = RequestBody.create(Utils.toJson(registerRequest), ApiClient.JSON);
        Request request = new Request.Builder()
                .url(Utils.BASE_URL+ "/auth/signup")
                .post(requestBody)
                .build();

        Response response = ApiClient.sendAsync(request).get();
        if (response == null) {
            throw new IOException("Send request fail: " + request);
        }

        if (!response.isSuccessful()) {
            throw new Exception("Username already exists");
        }

        login(new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword()));

        return true;
    }

    public int refreshToken() throws ExecutionException, InterruptedException, IOException {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(Utils.getRefreshToken());
        //build request
        RequestBody requestBody = RequestBody.create(Utils.toJson(refreshTokenRequest), ApiClient.JSON);
        Request request = new Request.Builder()
                .url(Utils.BASE_URL + "/auth/refreshToken")
                .post(requestBody)
                .build();

        Log.i(TAG,"Refresh token with refresh_token: " + Utils.getRefreshToken());
        Response response = ApiClient.sendAsync(request).get();
        RefreshTokenResponse tokenResponse = Utils.fromJson(response.body().string(), RefreshTokenResponse.class);
        LoginResponse auth = SharedPrefs.getInstance().get("auth", LoginResponse.class);
        auth.setAccessToken(tokenResponse.getAccessToken());
        sharedPrefs.put("auth", auth);
        return response.code();
    }

    public void logout() {
        Log.i(TAG, "Logout...");
        sharedPrefs.clear();
        Context context = App.self();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
