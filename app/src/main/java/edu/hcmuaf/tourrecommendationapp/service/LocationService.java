package edu.hcmuaf.tourrecommendationapp.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.dto.ProfileRequest;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class LocationService {
    private static final String USER_PATH = "/api/locations";
    private static LocationService mInstance;
    SharedPrefs sharedPrefs;

    private LocationService() {
        sharedPrefs = SharedPrefs.getInstance();
    }

    public static LocationService getInstance() {
        if (mInstance == null)
            mInstance = new LocationService();
        return mInstance;
    }

    public List<Location> getTopRatingLocation(int limit) throws IOException, ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Utils.BASE_URL + USER_PATH + "/top").newBuilder();
        urlBuilder.addQueryParameter("limit", String.valueOf(limit));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        return Arrays.asList(Utils.fromJson(response.body().string(), Location[].class));
    }

    public List<Location> filter(String query) throws IOException, ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Utils.BASE_URL + USER_PATH).newBuilder();
        urlBuilder.addQueryParameter("name", String.valueOf(query));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        return Arrays.asList(Utils.fromJson(response.body().string(), Location[].class));
    }


}
