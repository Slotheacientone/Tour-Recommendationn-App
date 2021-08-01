package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

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
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Location location = (Location) intent.getSerializableExtra("location");
        setTitle(location.getLocationName());

        if (savedInstanceState == null) {
            Bundle commentBundle = new Bundle();
            commentBundle.putLong("locationId", location.getLocationId());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rating_fragment_container_view, RatingsFragment.class, commentBundle)
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
                boolean success = false;
                try {
                    success = wishlistService.addLocationToWishlist(user.getId(), location.getLocationId());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (success) {
                    Toast.makeText(getBaseContext(), "Add to wishlist successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Add to wishlist unsuccessfully", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}