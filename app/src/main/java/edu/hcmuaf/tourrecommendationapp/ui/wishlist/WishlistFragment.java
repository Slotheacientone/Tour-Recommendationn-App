package edu.hcmuaf.tourrecommendationapp.ui.wishlist;

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

public class WishlistFragment extends Fragment {

    private RecyclerView wishlistRecycleView;
    private RecycleViewWishlistAdapter adapter;


    public WishlistFragment() {
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
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wishlistRecycleView = view.findViewById(R.id.wishlist_recycler_view);
        adapter = new RecycleViewWishlistAdapter(getContext());
        wishlistRecycleView.setAdapter(adapter);
        wishlistRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}