package edu.hcmuaf.tourrecommendationapp.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.LocationDetail;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class NearbyPlacesAPIService {
    public static final String TAG = "Nearby Places API";
    private static NearbyPlacesAPIService mInstance;
    private static final String PROXIMITY_RADIUS = "5000";

    private NearbyPlacesAPIService() {
    }

    public static NearbyPlacesAPIService getInstance() {
        if (mInstance == null)
            mInstance = new NearbyPlacesAPIService();
        return mInstance;
    }

    public List<Location> getNearByLocations(double latitude, double longitude, String type) throws IOException, JSONException {
        String apiKey = Resource.getString(R.string.google_maps_key);
        List<Location> locations = new ArrayList<Location>();
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.nearby_place_api_path)).newBuilder();
        String currentLocation = latitude + "," + longitude;
        urlBuilder.addQueryParameter("location", currentLocation);
        urlBuilder.addQueryParameter("radius", PROXIMITY_RADIUS);
        urlBuilder.addQueryParameter("type", type);
        urlBuilder.addQueryParameter("key", apiKey);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            String json = response.body().string();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray results = jsonObject.getJSONArray("results");
            if (jsonObject.getString("status").equalsIgnoreCase("OK")) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);

                    String placeId = place.getString("place_id");
                    String placeName = "";
                    if (!place.isNull("name")) {
                         placeName = place.getString("name");
                    }
                    latitude = place.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    longitude = place.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    Location location = new Location();
                    location.setLocationName(placeName);
                    location.setLocationLatitude(latitude);
                    location.setLocationLongitude(longitude);
                    location.setPlaceId(placeId);
                    locations.add(location);
                }

            }

        }
        return locations;
    }

    public LocationDetail getLocationDetail(String placeId) throws IOException, JSONException {
        String apiKey = Resource.getString(R.string.google_maps_key);
        LocationDetail locationDetail = new LocationDetail();
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.detail_place_api_path)).newBuilder();
        urlBuilder.addQueryParameter("place_id", placeId);
        urlBuilder.addQueryParameter("key", apiKey);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            String json = response.body().string();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject result = jsonObject.getJSONObject("result");
            if (jsonObject.getString("status").equalsIgnoreCase("OK")) {
                String name = result.getString("name");
                if(!result.isNull("formatted_address")) {
                    locationDetail.setAddress(result.getString("formatted_address"));
                }
                if(!result.isNull("formatted_phone_number")) {
                    locationDetail.setPhoneNumber(result.getString("formatted_phone_number"));
                }
                if(!result.isNull("opening_hours")) {
                    JSONObject openingHour = result.getJSONObject("opening_hours");
                    if(!result.isNull("opening_hours")) {
                        locationDetail.setOpenNow(openingHour.getBoolean("open_now"));
                    }
                }
                if(!result.isNull("rating")) {
                    locationDetail.setRating(BigDecimal.valueOf(result.getDouble("rating")).floatValue());
                }
                if(!result.isNull("user_ratings_total")) {
                    locationDetail.setUserRatingTotal(result.getInt("user_ratings_total"));
                }
                locationDetail.setName(name);
            }

        }
        return locationDetail;
    }
}
