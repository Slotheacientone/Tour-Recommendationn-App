package edu.hcmuaf.tourrecommendationapp.ui.navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

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
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition cameraPosition;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private DirectionsApiService directionsApiService;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SavedTrip savedTrip;
    private Marker currentLocationMarker;
    private List<Marker> markers;
    private Polyline polyline;


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
        markers = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("Bản đồ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    @SuppressLint("PotentialBehaviorOverride")
    @SneakyThrows
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Turn on the My Location layer and the related control on the map.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        getInitDeviceLocation();

        addMarker();

        startCurrentLocationUpdate();

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                for (edu.hcmuaf.tourrecommendationapp.model.Location location : savedTrip.getSavedTripLocations()) {
                    if (location.getLocationName().equalsIgnoreCase(marker.getTitle())) {
                        location.setLocationLatitude(marker.getPosition().latitude);
                        location.setLocationLongitude(marker.getPosition().longitude);
                        getRoute(lastKnownLocation, savedTrip.getSavedTripLocations())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<Route>() {
                                    @Override
                                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Route route) {
                                        if (route != null) {
                                            polyline.remove();
                                            // Add polylines to the map.
                                            // Polylines are useful to show a route or some other connection between points.
                                            polyline = map.addPolyline(new PolylineOptions()
                                                    .clickable(false)
                                                    .addAll(route.getPolyline()));
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
                }
            }
        });
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

    private void getInitDeviceLocation() {
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
                                getRoute(lastKnownLocation, savedTrip.getSavedTripLocations())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableObserver<Route>() {
                                            @Override
                                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Route route) {
                                                if (route != null) {
                                                    // Add polylines to the map.
                                                    // Polylines are useful to show a route or some other connection between points.
                                                    polyline = map.addPolyline(new PolylineOptions()
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
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    public void addMarker() {
        List<edu.hcmuaf.tourrecommendationapp.model.Location> locations = savedTrip.getSavedTripLocations();
        if (lastKnownLocation != null) {
            currentLocationMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_current_location_24))
                    .title("Vị trí hiện tại"));
        }
        for (edu.hcmuaf.tourrecommendationapp.model.Location location : locations) {
            if (location.getLocationLatitude() != 0.0 && location.getLocationLongitude() != 0.0) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLocationLatitude(), location.getLocationLongitude()))
                        .title(location.getLocationName()).draggable(true));
                markers.add(marker);
            }
        }
    }


    private void startCurrentLocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null) {
                return;
            }
            lastKnownLocation = locationResult.getLastLocation();
            if (currentLocationMarker != null) {
                currentLocationMarker.setPosition(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            } else {
                currentLocationMarker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .title("Vị trí hiện tại"));
            }
        }
    };

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
