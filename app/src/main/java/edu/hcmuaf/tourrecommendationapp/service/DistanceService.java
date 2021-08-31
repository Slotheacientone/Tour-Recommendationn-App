package edu.hcmuaf.tourrecommendationapp.service;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.DistanceRequest;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DistanceService {

    private static DistanceService mInstance;
    public static final String TAG = "Distance service";

    private DistanceService() {
    }

    public static DistanceService getInstance() {
        if (mInstance == null)
            mInstance = new DistanceService();
        return mInstance;
    }

    public List<Location> getDistances(double latitude, double longitude, List<Location> locations) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.distance_api_path)
                + Resource.getString(R.string.get_distances_api_uri)).newBuilder();
        String json = Utils.toJson(new DistanceRequest(latitude, longitude, locations));
        System.out.println(json);
        RequestBody requestBody = RequestBody.create(Utils.toJson(new DistanceRequest(latitude, longitude, locations)), ApiClient.JSON);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type recommendationsType = new TypeToken<List<Location>>() {
        }.getType();
        if (response != null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), recommendationsType);
        }
        return new ArrayList<Location>();
    }
}
