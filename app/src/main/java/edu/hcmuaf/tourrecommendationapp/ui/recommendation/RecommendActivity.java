package edu.hcmuaf.tourrecommendationapp.ui.recommendation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class RecommendActivity extends AppCompatActivity {

    private RecyclerView wishlistRecycleView;
    private RecycleViewWishlistRecommendationAdapter wishlistAdapter;
    private RecyclerView recommendationRecycleView;
    private RecycleViewRecommendationAdapter recommendationAdapter;
    private List<Location> selectedLocations;
    private SavedTripService savedTripService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        savedTripService = SavedTripService.getInstance();
        selectedLocations = new ArrayList<>();
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        wishlistRecycleView = findViewById(R.id.recommend_wishlist_recycler_view);
        wishlistAdapter = new RecycleViewWishlistRecommendationAdapter(this, selectedLocations);
        wishlistRecycleView.setAdapter(wishlistAdapter);
        wishlistRecycleView.setLayoutManager(new LinearLayoutManager(this));
        recommendationRecycleView = findViewById(R.id.recommend_recycler_view);
        recommendationAdapter = new RecycleViewRecommendationAdapter(this, selectedLocations);
        recommendationRecycleView.setAdapter(recommendationAdapter);
        recommendationRecycleView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recommended location");
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_path_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.find_path_menu_item:
                if(selectedLocations.size()>0) {
                    SavedTrip savedTrip = new SavedTrip();
                    savedTrip.setSavedTripLocations(selectedLocations);
                    System.out.print(selectedLocations);
                    savedTrip.setUserId(user.getId());
                    try {
                        savedTripService.saveTrip(savedTrip);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}