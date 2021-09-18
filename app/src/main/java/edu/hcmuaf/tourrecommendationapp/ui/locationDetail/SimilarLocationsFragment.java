package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.RecommendateService;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.ui.wishlist.RecycleViewWishlistAdapter;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SimilarLocationsFragment extends Fragment {
    private long locationId;
    private RecyclerView similarLocationsRecyclerView;
    private RecommendateService recommendateService;
    private List<Location> similarLocations;
    private RecycleViewSimilarLocationsAdapter adapter;

    public SimilarLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationId = requireArguments().getLong("locationId");
        recommendateService = RecommendateService.getInstance();
        similarLocations = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_similar_locations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        similarLocationsRecyclerView = view.findViewById(R.id.similar_locations_recycler_view);
        adapter = new RecycleViewSimilarLocationsAdapter(getContext(), similarLocations);
        similarLocationsRecyclerView.setAdapter(adapter);
        getSimilarLocations(locationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Location>>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Location> locations) {
                        similarLocations.addAll(locations);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(RecommendateService.TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        adapter.notifyDataSetChanged();
                    }
                });
        similarLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private Observable<List<Location>> getSimilarLocations(long locationId) {
        return Observable.create(emitter -> {
            try {
                List<Location> result = recommendateService.getSimilarLocations(locationId);
                emitter.onNext(result);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}