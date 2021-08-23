package edu.hcmuaf.tourrecommendationapp.ui.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Route;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.service.DirectionsApiService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private GoogleMap map;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private LatLng defaultLocation = new LatLng(10.762622, 106.660172);
    private float DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition cameraPosition;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private DirectionsApiService directionsApiService;


    // [START maps_poly_activity_on_polyline_click]
//    private static final int PATTERN_GAP_LENGTH_PX = 20;
//    private static final PatternItem DOT = new Dot();
//    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    //  private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);


    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SavedTrip savedTrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        directionsApiService = DirectionsApiService.getInstance();
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        Intent intent = getIntent();
        savedTrip = (SavedTrip) intent.getSerializableExtra("savedTrip");
        lastKnownLocation = intent.getParcelableExtra("location");
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("Saved trip detail");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SneakyThrows
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Turn on the My Location layer and the related control on the map.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        addMarker();

        // Set listeners for click events.
//        googleMap.setOnPolylineClickListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @SneakyThrows
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                                        .title("Vị trí hiện tại"));
                                getRoute(lastKnownLocation, savedTrip.getSavedTripLocations())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableObserver<Route>() {
                                            @Override
                                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Route route) {
                                                if (route != null) {
                                                    // Add polylines to the map.
                                                    // Polylines are useful to show a route or some other connection between points.
                                                    map.addPolyline(new PolylineOptions()
                                                            .clickable(false)
                                                            .addAll(route.getPolyline()));
                                                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(route.getBounds(), 0));
                                                }
                                            }

                                            @Override
                                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                                Log.e(DirectionsApiService.TAG, e.getMessage());
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }
                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            map.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
//                            map.getUiSettings().setMyLocationButtonEnabled(false);
                            finish();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

//    private void updateLocationUI() {
//        if (map == null) {
//            return;
//        }
//        try {
//            if (locationPermissionGranted) {
//                map.setMyLocationEnabled(true);
//                map.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                map.setMyLocationEnabled(false);
//                map.getUiSettings().setMyLocationButtonEnabled(false);
//                lastKnownLocation = null;
//                getLocationPermission();
//            }
//        } catch (SecurityException e) {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }

    public void addMarker() {
        List<edu.hcmuaf.tourrecommendationapp.model.Location> locations = savedTrip.getSavedTripLocations();
        if (lastKnownLocation != null) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                    .title("Vị trí hiện tại"));
        }
        for (edu.hcmuaf.tourrecommendationapp.model.Location location : locations) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLocationLatitude(), location.getLocationLongitude()))
                    .title(location.getLocationName()));
        }
    }


//    @Override
//    public void onPolylineClick(Polyline polyline) {
//        // Flip from solid stroke to dotted stroke pattern.
//        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
//            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
//        } else {
//            // The default pattern is a solid stroke.
//            polyline.setPattern(null);
//        }
//
//        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
//                Toast.LENGTH_SHORT).show();
//
//    }

    private Observable<Route> getRoute(Location origin, List<edu.hcmuaf.tourrecommendationapp.model.Location> waypoints) {
        return Observable.create(new ObservableOnSubscribe<Route>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Route> emitter) {
                try {
                    Route route = directionsApiService.getRoute(origin, waypoints);
                    emitter.onNext(route);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}