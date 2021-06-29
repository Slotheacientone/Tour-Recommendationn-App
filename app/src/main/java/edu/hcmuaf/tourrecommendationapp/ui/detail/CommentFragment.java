package edu.hcmuaf.tourrecommendationapp.ui.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Comment;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class CommentFragment extends Fragment {

    private RecyclerView commentList;
    private ImageView avatar;
    private EditText comment;
    private ImageButton sendButton;
    private RatingBar ratingBar;
    private List<Comment> comments = new ArrayList<Comment>();
    private long locationId;


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
      //  long userId = SharedPrefs.getInstance().get("userId", Long.class);
        long userId = 958;
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
                    createComment(commentString, userId, locationId, rating, adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                comment.setText("");
            }
        });
    }

    //    public void requestAvatar() {
//        String email = SharedPrefs.getInstance().get("email", String.class);
//        String token = SharedPrefs.getInstance().get("token", String.class);
//        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
//        String url = "http://192.168.1.22:8080/api/profile/" + email;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                System.out.println(response);
//                User user = new Gson().fromJson(response, User.class);
//                Picasso.get().load(user.getAvatar()).transform(new CropCircleTransformation()).into(avatar);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println(error);
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                // String accessToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzbG92ZXJzaW9uMkBnbWFpbC5jb20iLCJpYXQiOjE2MTA5NTYyNjAsImV4cCI6MTYxMTA0MjY2MH0.RgW5acxGapHkb_KD57LlIPsMhhN9EsNYkSa6caDlF-wozF5NUtJqn7VvVB074H4ErV3LeWCG-IFRFqVq8Qr_8nTlToYxX8qCw-TILTyQ9BAUhOzxblZf-cp34ORo1X6VGbFCNfvubX1w0SGfyJmus8e9BZszwmAIDcFZlA5dUJgEDh4IXi5nVNil6C58PpXmVKH0XSRH01z-ufy_mOcTmA_AUd4fKfyx7Dtju07JMJ49TNt0oBYLB7Qt2VPBo7gUcq_PqWDDZ0CxQANUGGXXPanidWCuiCzXwumhUtnZDutAzgeTj0xkM6fR_FFZZVtJSIb9fQn0cL_LV5T6HDrjLA";
//                Map<String, String> header = new HashMap<String, String>();
//                header.put("Authorization", "Bearer " + token);
//                return header;
//            }
//        };
//        requestQueue.add(stringRequest);
//    }
//
    private void createComment(String comment, long userId, long locationId, float locationRating, RecycleViewCommentAdapter adapter) throws JSONException {
        JSONObject jsonObject = new JSONObject();
       // String token = SharedPrefs.getInstance().get("token", String.class);
        jsonObject.put("userId", userId);
        jsonObject.put("locationId", locationId);
        jsonObject.put("locationRating", locationRating);
        jsonObject.put("comment", comment);
        String requestUrl = getString(R.string.base_api_uri) + getString(R.string.register_user_rating_api_uri);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                requestComment(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) /*{
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + token);
                return header;
            }
        }*/;
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void requestComment(RecycleViewCommentAdapter adapter) {
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        String url = getString(R.string.base_api_uri) + getString(R.string.get_comment_api_uri) + "?locationId=957";
       // String token = SharedPrefs.getInstance().get("token", String.class);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type listType = new TypeToken<ArrayList<Comment>>() {
                }.getType();
                List<Comment> temp;
                temp = new Gson().fromJson(response, listType);
                comments.clear();
                comments.addAll(temp);
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        })/* {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // String accessToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzbG92ZXJzaW9uMkBnbWFpbC5jb20iLCJpYXQiOjE2MTA5NTYyNjAsImV4cCI6MTYxMTA0MjY2MH0.RgW5acxGapHkb_KD57LlIPsMhhN9EsNYkSa6caDlF-wozF5NUtJqn7VvVB074H4ErV3LeWCG-IFRFqVq8Qr_8nTlToYxX8qCw-TILTyQ9BAUhOzxblZf-cp34ORo1X6VGbFCNfvubX1w0SGfyJmus8e9BZszwmAIDcFZlA5dUJgEDh4IXi5nVNil6C58PpXmVKH0XSRH01z-ufy_mOcTmA_AUd4fKfyx7Dtju07JMJ49TNt0oBYLB7Qt2VPBo7gUcq_PqWDDZ0CxQANUGGXXPanidWCuiCzXwumhUtnZDutAzgeTj0xkM6fR_FFZZVtJSIb9fQn0cL_LV5T6HDrjLA";
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + token);
                return header;
            }
        }*/;
        requestQueue.add(stringRequest);
    }
}