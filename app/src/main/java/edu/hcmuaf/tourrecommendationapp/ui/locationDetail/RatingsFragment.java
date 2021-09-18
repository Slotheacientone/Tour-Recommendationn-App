package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Rating;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.RatingService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RatingsFragment extends Fragment {

    private CardView currentUserRatingCard;
    private TextView currentUserRatingName;
    private ImageView currentUserRatingAvatar;
    private ImageButton deleteButton;
    private RatingBar currentUserRatingBar;
    private TextView currentUserRatingcomment;
    private TextView currentUserRatingDate;
    private RecyclerView commentRecyclerView;
    private RatingBar ratingBar;
    private List<Rating> ratings = new ArrayList<>();
    private long locationId;
    private RatingService ratingService;
    private User user;
    private Context context;
    private RecycleViewRatingAdapter adapter;
    private DateFormat dateFormat;
    private Rating currentUserRating = null;
    private boolean isUserSetRatingBar = true;


    public RatingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        ratingService = RatingService.getInstance();
        context = this.getContext();
        locationId = requireArguments().getLong("locationId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ratings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserRatingCard = view.findViewById(R.id.current_user_rating_card);
        currentUserRatingName = view.findViewById(R.id.current_user_rating_name);
        currentUserRatingAvatar = view.findViewById(R.id.current_user_rating_avatar);
        deleteButton = view.findViewById(R.id.delete_rating_button);
        currentUserRatingBar = view.findViewById(R.id.current_user_rating_bar);
        currentUserRatingcomment = view.findViewById(R.id.current_user_rating_comment);
        currentUserRatingDate = view.findViewById(R.id.current_user_rating_date);
        commentRecyclerView = view.findViewById(R.id.rating_recycler_view);
        ratingBar = view.findViewById(R.id.comment_rating_bar);
        adapter = new RecycleViewRatingAdapter(context, ratings);
        requestRatings();
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRating(user.getId(), locationId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                                if (aBoolean) {
                                    hideCurrentComment(true);
                                    currentUserRating = null;
                                    isUserSetRatingBar = false;
                                    ratingBar.setRating(0F);
                                } else {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.e(RatingService.TAG, e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (isUserSetRatingBar) {
                    Intent intent = new Intent(context, RatingActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("locationId", locationId);
                    intent.putExtra("rating", rating);
                    if (currentUserRating != null) {
                        intent.putExtra("comment", currentUserRating.getComment());
                    }
                    startForResult.launch(intent);
                } else {
                    isUserSetRatingBar = true;
                }
            }
        });
    }

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            requestRatings();
                            break;
                        case Activity.RESULT_CANCELED:
                            if (currentUserRating != null) {
                                if (ratingBar.getRating() != currentUserRating.getRating()) {
                                    isUserSetRatingBar = false;
                                    ratingBar.setRating(currentUserRating.getRating());
                                }
                            } else {
                                isUserSetRatingBar = false;
                                ratingBar.setRating(0F);
                            }
                            break;
                    }
                }
            });


    private void requestRatings() {
        getRatings(locationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Rating>>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Rating> result) {
                        currentUserRating = ratingService.getCurrentUserComment(result);
                        if (currentUserRating != null) {
                            hideCurrentComment(false);
                            Picasso.get().load(currentUserRating.getAvatar()).transform(new CropCircleTransformation()).into(currentUserRatingAvatar);
                            currentUserRatingName.setText(currentUserRating.getUserName());
                            currentUserRatingBar.setRating(currentUserRating.getRating());
                            currentUserRatingcomment.setText(currentUserRating.getComment());
                            currentUserRatingDate.setText(dateFormat.format(currentUserRating.getDate()));
                            if (ratingBar.getRating() != currentUserRating.getRating()) {
                                isUserSetRatingBar = false;
                                ratingBar.setRating(currentUserRating.getRating());
                            }
                        } else {
                            hideCurrentComment(true);
                        }
                        ratings.clear();
                        ratings.addAll(result);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(RatingService.TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private Observable<List<Rating>> getRatings(long locationId) {
        return Observable.create(new ObservableOnSubscribe<List<Rating>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<List<Rating>> emitter) {
                try {
                    List<Rating> result = ratingService.getRatings(locationId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    private Observable<Boolean> deleteRating(long userId, long locationId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) {
                try {
                    boolean isSuccess = ratingService.deleteRating(userId, locationId);
                    emitter.onNext(isSuccess);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    private void hideCurrentComment(boolean hide) {
        if (hide) {
            currentUserRatingCard.setVisibility(View.GONE);
        } else {
            currentUserRatingCard.setVisibility(View.VISIBLE);
        }
    }
}