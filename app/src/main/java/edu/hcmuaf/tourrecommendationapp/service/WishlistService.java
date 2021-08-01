package edu.hcmuaf.tourrecommendationapp.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class WishlistService {

    private static WishlistService mInstance;

    private WishlistService() {
    }

    public static WishlistService getInstance() {
        if (mInstance == null)
            mInstance = new WishlistService();
        return mInstance;
    }

    public boolean addLocationToWishlist(long userId, long locationId) throws ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.add_location_to_wishlist_api_uri)).newBuilder();
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

    public List<Location> getWishlist(long userId) throws IOException, ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.get_wishlist_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        Type wishlistType = new TypeToken<List<Location>>() {
        }.getType();
        if (response!=null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), wishlistType);
        }
        return new ArrayList<Location>();
    }

    public boolean deleteLocationFromWishlist(long userId, long locationId) throws ExecutionException, InterruptedException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.delete_location_from_wishlist_api_uri)).newBuilder();
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
