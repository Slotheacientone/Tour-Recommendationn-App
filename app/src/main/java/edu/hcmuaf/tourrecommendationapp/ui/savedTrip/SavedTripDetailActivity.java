package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.ui.navigation.MapsActivity;

public class SavedTripDetailActivity extends AppCompatActivity {

    private RecyclerView savedTripDetailRecycleView;
    private RecycleViewSavedTripDetailAdapter recycleViewSavedTripDetailAdapter;
    private SavedTrip savedTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_trip_detail);
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        savedTripDetailRecycleView = findViewById(R.id.saved_trip_location_recycle_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved trip detail");
        Intent intent = getIntent();
        savedTrip = (SavedTrip) intent.getSerializableExtra("savedTrip");
        recycleViewSavedTripDetailAdapter = new RecycleViewSavedTripDetailAdapter(this, savedTrip.getSavedTripLocations(), savedTrip.getSavedTripId());
        savedTripDetailRecycleView.setAdapter(recycleViewSavedTripDetailAdapter);
        savedTripDetailRecycleView.setLayoutManager(new LinearLayoutManager(this));
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
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("savedTrip", savedTrip);
                startActivity(intent);
               return true;
        }
        return super.onOptionsItemSelected(item);
    }

}