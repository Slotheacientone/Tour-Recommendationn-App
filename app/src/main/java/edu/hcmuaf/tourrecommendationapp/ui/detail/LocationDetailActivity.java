package edu.hcmuaf.tourrecommendationapp.ui.detail;

import androidx.appcompat.app.AppCompatActivity;

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
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.LocationService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class LocationDetailActivity extends AppCompatActivity {

    private ImageView locationImage;
    private TextView locationName;
    private RatingBar locationRatingBar;
    private TextView numberOfPeopleRating;
    private LocationService locationService;

    /**
     * Location id.
     */
    private long locationId;
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
        Bundle locationBundle = getIntent().getExtras();
        setTitle(locationBundle.getString("locationName"));
        locationId = locationBundle.getLong("locationId");
        if (savedInstanceState == null) {
            Bundle commentBundle = new Bundle();
            commentBundle.putLong("locationId", locationId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.comment_fragment_container_view, CommentFragment.class, commentBundle)
                    .commit();
        }
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        locationService = LocationService.getInstance();
        locationImage = findViewById(R.id.location_image);
        locationName = findViewById(R.id.location_name);
        locationRatingBar = findViewById(R.id.location_rating_bar);
        numberOfPeopleRating = findViewById(R.id.number_of_people_rating);
        locationName.setText(locationBundle.getString("locationName"));
        locationRatingBar.setRating(locationBundle.getFloat("rating"));
        numberOfPeopleRating.setText(String.valueOf(locationBundle.getInt("numberOfPeopleRating")));
        Picasso.get().load(locationBundle.getString("locationImage")).into(locationImage);

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
                    success = locationService.addLocationToWishlist(user.getId(), locationId);
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