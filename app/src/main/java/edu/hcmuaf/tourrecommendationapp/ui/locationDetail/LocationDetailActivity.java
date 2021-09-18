package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocationDetailActivity extends AppCompatActivity {

    private ImageView locationImage;
    private TextView locationName;
    private RatingBar locationRatingBar;
    private TextView numberOfPeopleRating;
    private WishlistService wishlistService;

    /**
     * Location id.
     */
    private Location location;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        location = (Location) intent.getSerializableExtra("location");
        setTitle(location.getLocationName());

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putLong("locationId", location.getLocationId());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rating_fragment_container_view, RatingsFragment.class, bundle)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.similar_locations_fragment_container_view, SimilarLocationsFragment.class, bundle)
                    .commit();
        }
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        wishlistService = WishlistService.getInstance();
        locationImage = findViewById(R.id.location_image);
        locationName = findViewById(R.id.location_name);
        locationRatingBar = findViewById(R.id.location_rating_bar);
        numberOfPeopleRating = findViewById(R.id.number_of_people_rating);
        locationName.setText(location.getLocationName());
        locationRatingBar.setRating(location.getRatings());
        numberOfPeopleRating.setText(String.valueOf(location.getNumberOfPeopleRating()));
        Picasso.get().load(location.getLocationImageUrl()).into(locationImage);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wishlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.wishlist_menu_item:
                addLocationToWishlist(user.getId(),location.getLocationId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@NonNull Boolean aBoolean) {
                                if (aBoolean) {
                                    Toast.makeText(getBaseContext(), "Add to wishlist successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Add to wishlist unsuccessfully", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Observable<Boolean> addLocationToWishlist(long userId, long locationId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                try {
                    boolean success = wishlistService.addLocationToWishlist(userId, locationId);
                    emitter.onNext(success);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}