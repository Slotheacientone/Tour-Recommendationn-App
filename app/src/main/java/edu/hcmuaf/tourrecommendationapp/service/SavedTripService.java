package edu.hcmuaf.tourrecommendationapp.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.ApiResponse;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SavedTripService {
    private static SavedTripService mInstance;

    private SavedTripService() {
    }

    public static SavedTripService getInstance() {
        if (mInstance == null)
            mInstance = new SavedTripService();
        return mInstance;
    }

    public boolean saveTrip(SavedTrip trip) throws ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.saved_trip_api_path)
                + Resource.getString(R.string.save_trip_api_uri)).newBuilder();
        String url = urlBuilder.build().toString();
        RequestBody requestBody = RequestBody.create(Utils.toJson(trip), ApiClient.JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        ApiResponse response = ApiClient.sendAsyncTemp(request);
        if (response.getCode() == 200) {
            return true;
        }
        return false;
    }

    public List<SavedTrip> getSavedTriplist(long userId) throws IOException, ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.saved_trip_api_path)
                + Resource.getString(R.string.get_saved_trip_list_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApiResponse response = ApiClient.sendAsyncTemp(request);
        Type savedTripListType = new TypeToken<List<SavedTrip>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.getBody(), savedTripListType);
        }
        return new ArrayList<SavedTrip>();
    }

    public SavedTrip getSavedTrip(long savedTripId) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.saved_trip_api_path)
                + Resource.getString(R.string.get_saved_trip_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("savedTrip", String.valueOf(savedTripId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApiResponse response = ApiClient.sendAsyncTemp(request);
        return Utils.fromJson(response.getBody(), SavedTrip.class);
    }

    public boolean deleteSavedTrip(long savedTripId) throws ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.saved_trip_api_path)
                + Resource.getString(R.string.delete_saved_trip_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("savedTripId",String.valueOf(savedTripId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApiResponse response = ApiClient.sendAsyncTemp(request);
        if (response.getCode() == 200) {
            return true;
        }
        return false;
    }
}
