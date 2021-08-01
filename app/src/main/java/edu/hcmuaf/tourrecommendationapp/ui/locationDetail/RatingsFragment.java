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

import android.os.StrictMode;
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
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
        requestRatings(adapter);
        if (currentUserRating != null) {
            ratingBar.setRating(currentUserRating.getRating());
        }
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean success = ratingService.deleteRating(user.getId(), locationId);
                    if (success) {
                        currentUserRating = null;
                        ratingBar.setRating(0f);
                        requestRatings(adapter);
                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating != 0) {
                    Intent intent = new Intent(context, RatingActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("locationId", locationId);
                    intent.putExtra("rating", rating);
                    if (currentUserRating != null) {
                        intent.putExtra("comment", currentUserRating.getComment());
                    }
                    startForResult.launch(intent);
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
                            requestRatings(adapter);
                            ratingBar.setRating(currentUserRating.getRating());
                            break;
                        case Activity.RESULT_CANCELED:
                            if (currentUserRating != null) {
                                ratingBar.setRating(currentUserRating.getRating());
                            } else {
                                ratingBar.setRating(0F);
                            }
                            break;
                    }
                }
            });


    private void requestRatings(RecycleViewRatingAdapter adapter) {
        try {
            List<Rating> response = ratingService.getComments(locationId);
            currentUserRating = ratingService.getCurrentUserComment(response);
            if (currentUserRating != null) {
                hideCurrentComment(false);
                Picasso.get().load(currentUserRating.getAvatar()).transform(new CropCircleTransformation()).into(currentUserRatingAvatar);
                currentUserRatingName.setText(currentUserRating.getUserName());
                currentUserRatingBar.setRating(currentUserRating.getRating());
                currentUserRatingcomment.setText(currentUserRating.getComment());
                currentUserRatingDate.setText(dateFormat.format(currentUserRating.getDate()));
            } else {
                hideCurrentComment(true);
            }
            ratings.clear();
            ratings.addAll(response);
            adapter.notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void hideCurrentComment(boolean hide) {
        if (hide) {
            currentUserRatingCard.setVisibility(View.GONE);
        } else {
            currentUserRatingCard.setVisibility(View.VISIBLE);
        }
    }
}