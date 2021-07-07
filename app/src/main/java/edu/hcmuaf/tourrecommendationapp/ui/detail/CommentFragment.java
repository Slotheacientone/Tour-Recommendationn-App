package edu.hcmuaf.tourrecommendationapp.ui.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Comment;
import edu.hcmuaf.tourrecommendationapp.service.CommentService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class CommentFragment extends Fragment {

    private RecyclerView commentList;
    private ImageView avatar;
    private EditText comment;
    private ImageButton sendButton;
    private RatingBar ratingBar;
    private List<Comment> comments = new ArrayList<Comment>();
    private long locationId;
    private CommentService commentService;


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
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
            //  long userId = SharedPrefs.getInstance().get("userId", Long.class);
        long userId = 958;
        commentService = CommentService.getInstance();
        commentList = view.findViewById(R.id.comment_list);
        avatar = view.findViewById(R.id.comment_avatar);
        comment = view.findViewById(R.id.comment_edit_text);
        ratingBar = view.findViewById(R.id.rating_bar);
        sendButton = view.findViewById(R.id.send_button);
      //  requestAvatar();
        RecycleViewCommentAdapter adapter = new RecycleViewCommentAdapter(this.getContext(), comments);
        requestComment(adapter);
        commentList.setAdapter(adapter);
        commentList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentString = comment.getText().toString();
                float rating = ratingBar.getRating();
                try {
                    commentService.registerComment(commentString, userId, locationId, rating);
                    requestComment(adapter);
                } catch (ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                comment.setText("");
            }
        });
    }

    private void requestComment(RecycleViewCommentAdapter adapter) {
        locationId = 957;
        try {
            List<Comment> reponse = commentService.getComments(locationId);
            comments.clear();
            comments.addAll(reponse);
            adapter.notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}