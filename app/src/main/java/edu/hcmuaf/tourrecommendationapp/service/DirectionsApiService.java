package edu.hcmuaf.tourrecommendationapp.service;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.ApiResponse;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.Route;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class DirectionsApiService {

    public static final String TAG = "Direction api service";
    private SortService sortService;
    private static DirectionsApiService mInstance;

    private DirectionsApiService() {
        sortService = SortService.getInstance();
    }

    public static DirectionsApiService getInstance() {
        if (mInstance == null)
            mInstance = new DirectionsApiService();
        return mInstance;
    }

    public Route getRoute(android.location.Location origin, List<Location> waypoints) throws IOException, JSONException {
        Route route = null;
        sortService.sortByDistance(waypoints);
        Location destination = waypoints.get(waypoints.size() - 1);
        waypoints.remove(destination);
        String apiKey = Resource.getString(R.string.google_maps_key);
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.direction_api_path)).newBuilder();
        String originQueryParameter = prepareLocationQueryParameter(origin);
        urlBuilder.addQueryParameter("origin", originQueryParameter);
        String destinationQueryParameter = prepareLocationQueryParameter(destination);
        urlBuilder.addQueryParameter("destination", destinationQueryParameter);
        String waypointsQueryParameter = prepareWaypointsQueryParameter(waypoints);
        if (!waypointsQueryParameter.isEmpty()) {
            urlBuilder.addQueryParameter("waypoints", waypointsQueryParameter);
        }
        urlBuilder.addQueryParameter("key", apiKey);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i(TAG, "Send request: " + request);
        Response response = ApiClient.getClient().newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            route = new Route();
            String stringResponse = response.body().string();
            JSONObject json = new JSONObject(stringResponse);
            JSONArray routes = json.getJSONArray("routes");
            JSONObject routesObject = routes.getJSONObject(0);
            JSONObject overviewPolyline = routesObject.getJSONObject("overview_polyline");
            String encodedPath = overviewPolyline.getString("points");
            route.addAllPolyline(PolyUtil.decode(encodedPath));
            JSONObject bounds = routesObject.getJSONObject("bounds");
            JSONObject southwest = bounds.getJSONObject("southwest");
            LatLng southwestLatLng = new LatLng(southwest.getDouble("lat"), southwest.getDouble("lng"));
            JSONObject northeast = bounds.getJSONObject("northeast");
            LatLng northeastLatLng = new LatLng(northeast.getDouble("lat"), southwest.getDouble("lng"));
            LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
            route.setBounds(latLngBounds);
        }
        return route;
    }

    public String prepareWaypointsQueryParameter(List<Location> waypoints) {
        String waypointsQueryParameter = "";
        if (waypoints.size() > 0) {
            waypointsQueryParameter = "optimize:true";
            for (Location location : waypoints) {
                waypointsQueryParameter += "|" + prepareLocationQueryParameter(location);
            }
        }
        return waypointsQueryParameter;
    }

    public String prepareLocationQueryParameter(Location location) {
        String locationQueryParameter = "";
        if (location.getLocationLatitude() != 0.0 && location.getLocationLongitude() != 0.0) {
            locationQueryParameter = location.getLocationLatitude() + "," + location.getLocationLongitude();
        } else {
            locationQueryParameter = location.getLocationName();
        }
        return locationQueryParameter;
    }

    public String prepareLocationQueryParameter(android.location.Location location) {
        String locationQueryParameter = "";
        locationQueryParameter = location.getLatitude() + "," + location.getLongitude();
        return locationQueryParameter;
    }
}
