package edu.hcmuaf.tourrecommendationapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Comment;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentService {

    private static CommentService mInstance;

    private CommentService() {
    }

    public static CommentService getInstance() {
        if (mInstance == null)
            mInstance = new CommentService();
        return mInstance;
    }

    public boolean registerComment(String comment, long userId, long locationId, float locationRating) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.comment_path)
                + Resource.getString(R.string.register_user_rating_api_uri)).newBuilder();
        String url = urlBuilder.build().toString();
        RequestBody body = new FormBody.Builder()
                .add("userId", String.valueOf(userId))
                .add("locationId", String.valueOf(locationId))
                .add("locationRating", String.valueOf(locationRating))
                .add("comment", comment)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        if (response.code() == 200) {
            return true;
        }
        return false;
    }

    public List<Comment> getComments(long locationId) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.comment_path)
                + Resource.getString(R.string.get_comment_api_uri)).newBuilder();
        urlBuilder.addQueryParameter("locationId", String.valueOf(locationId));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = ApiClient.sendAsync(request).get();
        Type commentsType = new TypeToken<List<Comment>>() {
        }.getType();
        Gson gson = new Gson();
        String responseBody = response.body().string();
        System.out.println("body: " + responseBody);
        return null;
    }
}
