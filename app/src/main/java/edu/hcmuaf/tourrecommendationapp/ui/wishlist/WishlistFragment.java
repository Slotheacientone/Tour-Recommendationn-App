package edu.hcmuaf.tourrecommendationapp.ui.wishlist;

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
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;

public class WishlistFragment extends Fragment {

    private RecyclerView wishlistRecycleView;
    private RecycleViewWishlistAdapter adapter;
    private List<Location> wishlist;
    private WishlistService wishlistService;
    private User user;


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

    @SneakyThrows
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wishlistRecycleView = view.findViewById(R.id.wishlist_recycler_view);
        wishlist = new ArrayList<>();
        wishlistService = WishlistService.getInstance();
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        adapter = new RecycleViewWishlistAdapter(getContext(), wishlist);
        wishlistRecycleView.setAdapter(adapter);
        getWishlist(user.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Location>>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Location> locations) {
                wishlist.addAll(locations);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.e(WishlistService.TAG, e.getMessage());
            }

            @Override
            public void onComplete() {
                adapter.notifyDataSetChanged();
            }
        });
        wishlistRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private Observable<List<Location>> getWishlist(long userId) {
        return Observable.create(emitter -> {
            try {
                List<Location> result = wishlistService.getWishlist(userId);
                emitter.onNext(result);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}