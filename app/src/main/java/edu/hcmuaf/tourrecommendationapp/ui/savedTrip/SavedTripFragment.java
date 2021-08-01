package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.ui.wishlist.RecycleViewWishlistAdapter;

public class SavedTripFragment extends Fragment {

    private RecyclerView savedTripRecycleView;
    private RecycleViewSavedTripAdapter adapter;

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
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedTripRecycleView = view.findViewById(R.id.saved_trip_recycler_view);
        adapter = new RecycleViewSavedTripAdapter(getContext());
        savedTripRecycleView.setAdapter(adapter);
        savedTripRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}