package edu.hcmuaf.tourrecommendationapp.ui.recommendation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.DistanceService;
import edu.hcmuaf.tourrecommendationapp.service.RecommendateService;
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import edu.hcmuaf.tourrecommendationapp.service.SortService;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.ui.navigation.MapsActivity;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import lombok.SneakyThrows;

public class RecommendActivity extends AppCompatActivity {

    private ToggleButton toggleSortRecommendButton;
    private RecyclerView wishlistRecycleView;
    private RecycleViewWishlistRecommendationAdapter wishlistAdapter;
    private RecyclerView recommendationRecycleView;
    private RecycleViewRecommendationAdapter recommendationAdapter;
    private List<Location> recommendations;
    private List<Location> wishlist;
    private SavedTripService savedTripService;
    private SortService sortService;
    private User user;
    private boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private android.location.Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecommendateService recommendateService;
    private WishlistService wishlistService;
    private DistanceService distanceService;
    private boolean haveDistances = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        toggleSortRecommendButton = findViewById(R.id.toggle_sort_recommen_button);
        savedTripService = SavedTripService.getInstance();
        sortService = SortService.getInstance();
        recommendations = new ArrayList<>();
        wishlist = new ArrayList<>();
        recommendateService = RecommendateService.getInstance();
        wishlistService = WishlistService.getInstance();
        distanceService = DistanceService.getInstance();
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recommended location");
        toggleSortRecommendButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sortService.sortByDistance(recommendations);
                    sortService.sortByDistance(wishlist);
                } else {
                    sortService.sortByRecommendScore(recommendations);
                    sortService.sortByWishListOrder(wishlist);
                }
                recommendationAdapter.notifyDataSetChanged();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        if (locationPermissionGranted) {
            getDeviceLocation();
        }
        prepareData();
        recommendationRecycleView = findViewById(R.id.recommend_recycler_view);
        recommendationAdapter = new RecycleViewRecommendationAdapter(this, recommendations);
        recommendationRecycleView.setAdapter(recommendationAdapter);
        recommendationRecycleView.setLayoutManager(new LinearLayoutManager(this));
        wishlistRecycleView = findViewById(R.id.recommend_wishlist_recycler_view);
        wishlistAdapter = new RecycleViewWishlistRecommendationAdapter(this, wishlist);
        wishlistRecycleView.setAdapter(wishlistAdapter);
        wishlistRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_path_menu, menu);
        return true;
    }

    public void prepareData() {
        try {
            if (this.lastKnownLocation != null) {
                recommendations.addAll(recommendateService.getRecommendations(user.getId(), this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude()));
                wishlist.addAll(wishlistService.getWishlist(user.getId(), this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude()));
                haveDistances = true;
            } else {
                recommendations.addAll(recommendateService.getRecommendations(user.getId()));
               // wishlist.addAll(wishlistService.getWishlist(user.getId()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.find_path_menu_item:
                List<Location> selectedLocations = new ArrayList<>();
                for (Location location : recommendations) {
                    if (location.isSelected()) {
                        selectedLocations.add(location);
                    }
                }
                for (Location location : wishlist) {
                    if (location.isSelected()) {
                        selectedLocations.add(location);
                    }
                }
                if (selectedLocations.size() > 0) {
                    SavedTrip savedTrip = new SavedTrip();
                    savedTrip.setSavedTripLocations(selectedLocations);
                    savedTrip.setUserId(user.getId());
                    try {
                        savedTripService.saveTrip(savedTrip);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("savedTrip", savedTrip);
                    if(lastKnownLocation!=null) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("location", lastKnownLocation);
                        startActivity(intent, bundle);
                    }else {
                        startActivity(intent);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                Task<android.location.Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<android.location.Location>() {
                    @SneakyThrows
                    @Override
                    public void onComplete(@NonNull Task<android.location.Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (!haveDistances&&lastKnownLocation!=null) {
                                List<Location> recommendationDistance = distanceService.getDistance(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), recommendations);
                                for (int i = 0; i < recommendationDistance.size(); i++) {
                                    recommendations.get(i).setDistance(recommendationDistance.get(i).getDistance());
                                }
                                List<Location> wishlistDistance = distanceService.getDistance(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), wishlist);
                                for (int i = 0; i < wishlistDistance.size(); i++) {
                                    wishlist.get(i).setDistance(wishlistDistance.get(i).getDistance());
                                }
                                recommendationAdapter.notifyDataSetChanged();
                                wishlistAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

}