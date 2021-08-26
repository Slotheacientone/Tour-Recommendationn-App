package edu.hcmuaf.tourrecommendationapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;

import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.service.LocationService;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;


public class HomeFragment extends Fragment {
    private RecycleViewTopRatingAdapter topRatingAdapter;
    private RecyclerView topRatingRecycleView;
    private LocationService locationService;
    private List<Location> topRatingLocation;

    public HomeFragment() {
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SneakyThrows
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //DI
        locationService = LocationService.getInstance();
        topRatingLocation = new ArrayList<>();

        topRatingRecycleView = view.findViewById(R.id.topRatingRecycleView);
        topRatingAdapter = new RecycleViewTopRatingAdapter(getContext(), topRatingLocation);
        topRatingRecycleView.setAdapter(topRatingAdapter);

        getTopRatingLocation(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Location>>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Location> locations) {
                        topRatingLocation.addAll(locations);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        topRatingAdapter.notifyDataSetChanged();
                    }
                });
        topRatingRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private Observable<List<Location>> getTopRatingLocation(int limit) {
        return Observable.create(emitter -> {
            try {
                List<Location> result = locationService.getTopRatingLocation(limit);
                emitter.onNext(result);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

}