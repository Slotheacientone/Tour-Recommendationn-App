package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

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
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SavedTripFragment extends Fragment {

    private RecyclerView savedTripRecycleView;
    private RecycleViewSavedTripAdapter adapter;
    private List<SavedTrip> savedTrips;
    private SavedTripService savedTripService;
    private User user;

    public SavedTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_trip, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSavedTrips(user.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<SavedTrip>>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<SavedTrip> savedTripList) {
                        savedTrips.clear();
                        savedTrips.addAll(savedTripList);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(SavedTripService.TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedTripRecycleView = view.findViewById(R.id.saved_trip_recycler_view);
        savedTrips = new ArrayList<>();
        savedTripService = SavedTripService.getInstance();
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        adapter = new RecycleViewSavedTripAdapter(getContext(), savedTrips);
        savedTripRecycleView.setAdapter(adapter);
        savedTripRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private Observable<List<SavedTrip>> getSavedTrips(long userId) {
        return Observable.create(new ObservableOnSubscribe<List<SavedTrip>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<List<SavedTrip>> emitter) {
                try {
                    List<SavedTrip> result = savedTripService.getSavedTrips(userId);
                    System.out.println(result);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }

            }
        });
    }
}