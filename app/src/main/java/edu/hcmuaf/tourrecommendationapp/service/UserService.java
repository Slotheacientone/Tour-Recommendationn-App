package edu.hcmuaf.tourrecommendationapp.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class UserService {
    private static final String USER_PATH = "/api/users";
    private static UserService mInstance;

    private UserService() {
    }

    public static UserService getInstance() {
        if (mInstance == null)
            mInstance = new UserService();
        return mInstance;
    }

    public User getInfo(String username) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Utils.BASE_URL + USER_PATH).newBuilder();
        urlBuilder.addPathSegment(username);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = ApiClient.sendAsync(request).get();
        return Utils.fromJson(response.body().string(), User.class);
    }


}
