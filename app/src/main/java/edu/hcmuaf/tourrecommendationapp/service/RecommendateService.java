package edu.hcmuaf.tourrecommendationapp.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendateService {

    private static RecommendateService mInstance;

    private RecommendateService() {
    }

    public static RecommendateService getInstance() {
        if (mInstance == null)
            mInstance = new RecommendateService();
        return mInstance;
    }

    public List<Location> getRecommendations(long userId) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.recommendate_api_path)
                + Resource.getString(R.string.get_recommendation_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        Type recommendationsType = new TypeToken<List<Location>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), recommendationsType);
        }
        return new ArrayList<Location>();
    }
}
