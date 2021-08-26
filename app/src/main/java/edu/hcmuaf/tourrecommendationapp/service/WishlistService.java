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

public class WishlistService {

    private static WishlistService mInstance;
    public final static String TAG = "Wishlist service";

    private WishlistService() {
    }

    public static WishlistService getInstance() {
        if (mInstance == null)
            mInstance = new WishlistService();
        return mInstance;
    }


    public boolean addLocationToWishlist(long userId, long locationId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.add_location_to_wishlist_api_uri)).newBuilder();
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

    public List<Location> getWishlist(long userId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.get_wishlist_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type wishlistType = new TypeToken<List<Location>>() {
        }.getType();
        if (response != null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), wishlistType);
        }
        return new ArrayList<Location>();
    }

    public boolean deleteLocationFromWishlist(long userId, long locationId) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.delete_location_from_wishlist_api_uri)).newBuilder();
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

    public List<Location> getWishlist(long userId, double latitude, double longitude) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.wishlist_api_path)
                + Resource.getString(R.string.get_wishlist_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        urlBuilder.addQueryParameter("latitude", String.valueOf(latitude));
        urlBuilder.addQueryParameter("longitude", String.valueOf(longitude));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        Type wishlistType = new TypeToken<List<Location>>() {
        }.getType();
        if (response != null && response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), wishlistType);
        }
        return new ArrayList<Location>();

    }
}
