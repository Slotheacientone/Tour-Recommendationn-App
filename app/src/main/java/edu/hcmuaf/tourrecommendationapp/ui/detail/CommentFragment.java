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
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

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
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.CommentService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class CommentFragment extends Fragment {

    private RecyclerView commentList;
    private TextView userName;
    private ImageView avatar;
    private EditText comment;
    private ImageButton sendButton;
    private RatingBar ratingBar;
    private List<Comment> comments = new ArrayList<Comment>();
    private long locationId;
    private CommentService commentService;
    private User user;


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
        System.out.println(user.getId());
        System.out.println(user.getUsername());
        commentService = CommentService.getInstance();
        commentList = view.findViewById(R.id.comment_list);
        userName = view.findViewById(R.id.user_name);
        avatar = view.findViewById(R.id.comment_avatar);
        comment = view.findViewById(R.id.comment_edit_text);
        ratingBar = view.findViewById(R.id.rating_bar);
        sendButton = view.findViewById(R.id.send_button);
        userName.setText(user.getUsername());
        Picasso.get().load(user.getThumbnail()).into(avatar);
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
                    commentService.registerComment(commentString, user.getId(), locationId, rating);
                    requestComment(adapter);
                } catch (ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                comment.setText("");
            }
        });
    }

    private void requestComment(RecycleViewCommentAdapter adapter) {
        try {
            List<Comment> response = commentService.getComments(locationId);
            comments.clear();
            comments.addAll(response);
            adapter.notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}