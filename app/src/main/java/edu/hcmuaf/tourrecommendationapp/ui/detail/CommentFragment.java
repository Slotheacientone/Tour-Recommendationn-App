package edu.hcmuaf.tourrecommendationapp.ui.detail;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Comment;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.CommentService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class CommentFragment extends Fragment {

    private RecyclerView commentRecyclerView;
    private RatingBar ratingBar;
    private List<Comment> comments = new ArrayList<>();
    private long locationId;
    private float userRating;
    private CommentService commentService;
    private User user;
    private Context context;
    private RecycleViewCommentAdapter adapter;


    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationId = requireArguments().getLong("locationId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
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
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        commentService = CommentService.getInstance();
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        ratingBar = view.findViewById(R.id.comment_rating_bar);
        context = this.getContext();
        adapter = new RecycleViewCommentAdapter(context, comments);
        Comment currentUserComment = requestComment(adapter);
        if (currentUserComment != null){
            userRating = currentUserComment.getRating();
            ratingBar.setRating(userRating);
        }
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating!=userRating) {
                    Intent intent = new Intent(context, RatingActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("locationId", locationId);
                    intent.putExtra("rating", rating);
                    if (currentUserComment != null) {
                        intent.putExtra("comment", currentUserComment.getComment());
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
                            Comment currentUserComment = requestComment(adapter);
                            if(currentUserComment!=null) {
                                userRating = currentUserComment.getRating();
                                ratingBar.setRating(currentUserComment.getRating());
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            ratingBar.setRating(userRating);
                            break;
                    }
                }
            });


    private Comment requestComment(RecycleViewCommentAdapter adapter) {
        Comment currentUserComment = null;
        try {
            List<Comment> response = commentService.getComments(locationId);
            currentUserComment = commentService.getCurrentUserComment(response);
            comments.clear();
            comments.addAll(response);
            adapter.notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return currentUserComment;
    }
}