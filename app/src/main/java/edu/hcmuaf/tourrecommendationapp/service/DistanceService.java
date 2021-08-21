package edu.hcmuaf.tourrecommendationapp.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.DistanceRequest;
import edu.hcmuaf.tourrecommendationapp.model.ApiResponse;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DistanceService {

    private static DistanceService mInstance;

    private DistanceService(){
    }

    public static DistanceService getInstance() {
        if (mInstance == null)
            mInstance = new DistanceService();
        return mInstance;
    }

    public List<Location> getDistance(double latitude, double longitude, List<Location> locations) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.distance_api_path)
                + Resource.getString(R.string.get_distances_api_uri)).newBuilder();
        RequestBody requestBody = RequestBody.create(Utils.toJson(new DistanceRequest(latitude, longitude, locations)), ApiClient.JSON);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        ApiResponse response = ApiClient.sendAsyncTemp(request);
        Type recommendationsType = new TypeToken<List<Location>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.getBody(), recommendationsType);
        }
        return new ArrayList<Location>();
    }
}
