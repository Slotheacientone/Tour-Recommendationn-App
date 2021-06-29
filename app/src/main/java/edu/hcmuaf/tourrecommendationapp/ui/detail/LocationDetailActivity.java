package edu.hcmuaf.tourrecommendationapp.ui.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.hcmuaf.tourrecommendationapp.R;

public class LocationDetailActivity extends AppCompatActivity {

    private ImageView locationImage;
    private TextView locationName;
    private RatingBar locationRatingBar;
    private TextView numberOfPeopleRating;

    /** Location id. */
    private long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        Bundle locationBundle = getIntent().getExtras();
        locationId = locationBundle.getLong("locationId");
        if (savedInstanceState == null) {
            Bundle commentBundle = new Bundle();
            commentBundle.putLong("locationId", locationId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.comment_fragment_container_view, CommentFragment.class, commentBundle)
                    .commit();
        }
        locationImage = findViewById(R.id.location_image);
        locationName = findViewById(R.id.location_name);
        locationRatingBar = findViewById(R.id.location_rating_bar);
        numberOfPeopleRating = findViewById(R.id.number_of_people_rating);
        locationName.setText(locationBundle.getString("locationName"));
        locationRatingBar.setRating(locationBundle.getFloat("rating"));
        numberOfPeopleRating.setText(String.valueOf(locationBundle.getInt("numberOfPeopleRating")));
        Picasso.get().load(locationBundle.getString("locationImage")).into(locationImage);

    }
}