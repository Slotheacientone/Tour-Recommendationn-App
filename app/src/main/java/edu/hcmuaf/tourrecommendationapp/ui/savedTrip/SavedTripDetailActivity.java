package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import edu.hcmuaf.tourrecommendationapp.ui.navigation.MapsActivity;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SavedTripDetailActivity extends AppCompatActivity {

    private RecyclerView savedTripDetailRecycleView;
    private RecycleViewSavedTripDetailAdapter recycleViewSavedTripDetailAdapter;
    private long savedTripId;
    private SavedTrip savedTrip;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_trip_detail);
        savedTripDetailRecycleView = findViewById(R.id.saved_trip_location_recycle_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved trip detail");
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        savedTripId = bundle.getLong("savedTripId");
        savedTrip = new SavedTrip();
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        savedTrip.setUserId(user.getId());
        savedTrip.setSavedTripLocations(new ArrayList<>());
        savedTrip.setSavedTripId(savedTripId);
        recycleViewSavedTripDetailAdapter = new RecycleViewSavedTripDetailAdapter(this, savedTrip.getSavedTripLocations(), savedTrip.getSavedTripId());
        savedTripDetailRecycleView.setAdapter(recycleViewSavedTripDetailAdapter);
        getSavedTrip().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SavedTrip>() {
                    @Override
                    public void onNext(@NonNull SavedTrip result) {
                        savedTrip.getSavedTripLocations().addAll(result.getSavedTripLocations());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(SavedTripService.TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        recycleViewSavedTripDetailAdapter.notifyDataSetChanged();
                    }
                });
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
                if (savedTrip != null) {
                    Intent intent = new Intent(this, MapsActivity.class);
                    System.out.println("Saved trip locations: " + savedTrip.getSavedTripLocations());
                    intent.putExtra("savedTrip", savedTrip);
                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Observable<SavedTrip> getSavedTrip() {
        return Observable.create(new ObservableOnSubscribe<SavedTrip>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<SavedTrip> emitter) {
                try {
                    SavedTrip savedTrip = SavedTripService.getInstance().getSavedTrip(savedTripId);
                    emitter.onNext(savedTrip);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }

            }
        });
    }

}