package edu.hcmuaf.tourrecommendationapp.service;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendateService {

    private static RecommendateService mInstance;
    public final static String TAG = "Recommendate service";

    private RecommendateService() {
    }

    public static RecommendateService getInstance() {
        if (mInstance == null)
            mInstance = new RecommendateService();
        return mInstance;
    }

    public List<Location> getRecommendations(long userId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.recommendate_api_path)
                + Resource.getString(R.string.get_recommendation_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type recommendationsType = new TypeToken<List<Location>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), recommendationsType);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getRecommendations(long userId, double latitude, double longitude) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.recommendate_api_path)
                + Resource.getString(R.string.get_recommendation_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        urlBuilder.addQueryParameter("latitude", String.valueOf(latitude));
        urlBuilder.addQueryParameter("longitude", String.valueOf(longitude));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type recommendationsType = new TypeToken<List<Location>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), recommendationsType);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getSimilarLocations(long locationId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.recommendate_api_path)
                + Resource.getString(R.string.get_similar_locations_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("locationId", String.valueOf(locationId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type similarLocationsType = new TypeToken<List<Location>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), similarLocationsType);
        }
        return new ArrayList<Location>();
    }
}
