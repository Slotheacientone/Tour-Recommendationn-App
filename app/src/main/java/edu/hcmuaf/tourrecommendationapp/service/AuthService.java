package edu.hcmuaf.tourrecommendationapp.service;

import java.io.IOException;

import edu.hcmuaf.tourrecommendationapp.dto.LoginRequest;
import edu.hcmuaf.tourrecommendationapp.dto.LoginResponse;
import edu.hcmuaf.tourrecommendationapp.dto.RegisterRequest;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {
    private static AuthService mInstance;
    private UserService userService;

    private AuthService() {
        userService = UserService.getInstance();
    }

    public static AuthService getInstance() {
        if (mInstance == null)
            mInstance = new AuthService();
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
        sharedPrefs.put("accessToken", loginResponse.getAccessToken());
        sharedPrefs.put("refreshToken", loginResponse.getRefreshToken());

        User user = userService.getInfo(loginRequest.getUsername());
        sharedPrefs.put("myInfo", user);

        return true;
    }

    private boolean validate(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        return username != null && username.length() >= 6 && password != null && password.length() >= 6;
    }

    private boolean validate(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        return username != null && username.length() >= 6 && password != null && password.length() >= 6;
    }

    public boolean register(RegisterRequest registerRequest) throws Exception {
        if (!validate(registerRequest))
            return false;

        //build request
        RequestBody requestBody = RequestBody.create(Utils.toJson(registerRequest), ApiClient.JSON);
        Request request = new Request.Builder()
                .url(Utils.BASE_URL + "/auth/signup")
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
}
