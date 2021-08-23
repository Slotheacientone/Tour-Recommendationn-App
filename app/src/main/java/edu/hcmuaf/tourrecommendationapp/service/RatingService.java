package edu.hcmuaf.tourrecommendationapp.service;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.RatingRequest;
import edu.hcmuaf.tourrecommendationapp.model.Rating;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RatingService {

    private static RatingService mInstance;
    public static final String TAG = "Rating service";

    private RatingService() {
    }

    public static RatingService getInstance() {
        if (mInstance == null)
            mInstance = new RatingService();
        return mInstance;
    }

    public boolean registerRating(RatingRequest ratingRequest) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.rating_api_path)
                + Resource.getString(R.string.register_user_rating_api_uri)).newBuilder();
        String url = urlBuilder.build().toString();
        RequestBody requestBody = RequestBody.create(Utils.toJson(ratingRequest), ApiClient.JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        if (response.code() == 200) {
            return true;
        }
        return false;
    }

    public List<Rating> getRatings(long locationId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.rating_api_path)
                + Resource.getString(R.string.get_rating_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("locationId", String.valueOf(locationId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type commentsType = new TypeToken<List<Rating>>() {
        }.getType();
        if (response != null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), commentsType);
        }
        return new ArrayList<Rating>();
    }

    public Rating getCurrentUserComment(List<Rating> ratings) {
        Rating result = null;
        long userId = SharedPrefs.getInstance().get("myInfo", User.class).getId();
        for (Rating rating : ratings) {
            if (rating.getUserId() == userId) {
                result = rating;
                ratings.remove(rating);
            }
        }
        return result;
    }

    public boolean deleteRating(long userId, long locationId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.rating_api_path)
                + Resource.getString(R.string.delete_rating_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        urlBuilder.addQueryParameter("locationId", String.valueOf(locationId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        if (response.code() == 200) {
            return true;
        }
        return false;
    }
}

