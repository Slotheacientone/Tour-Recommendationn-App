package edu.hcmuaf.tourrecommendationapp.service;

import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class LocationService {

    private static LocationService mInstance;

    private LocationService() {
    }

    public static LocationService getInstance() {
        if (mInstance == null)
            mInstance = new LocationService();
        return mInstance;
    }

    public boolean addLocationToWishlist(long userId, long locationId) throws ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_path)
                + Resource.getString(R.string.add_location_to_wishlist_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId",String.valueOf(userId));
        urlBuilder.addQueryParameter("locationId", String.valueOf(locationId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        if (response.code() == 200) {
            return true;
        }
        return false;
    }

}
