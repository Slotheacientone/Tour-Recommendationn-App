package edu.hcmuaf.tourrecommendationapp.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.CommentRequest;
import edu.hcmuaf.tourrecommendationapp.model.Comment;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.Resource;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
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

    public boolean registerComment(CommentRequest commentRequest) throws ExecutionException, InterruptedException, IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(Resource.getString(R.string.base_api_uri)
                + Resource.getString(R.string.comment_path)
                + Resource.getString(R.string.register_user_rating_api_uri)).newBuilder();
        String url = urlBuilder.build().toString();
        RequestBody requestBody = RequestBody.create(Utils.toJson(commentRequest), ApiClient.JSON);
        System.out.println(Utils.toJson(commentRequest));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
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
        if (response.isSuccessful()) {
            return Utils.fromJson(response.body().string(), commentsType);
        }
        return new ArrayList<Comment>();
    }

    public Comment getCurrentUserComment(List<Comment> comments) {
        Comment result = null;
        long userId = SharedPrefs.getInstance().get("myInfo", User.class).getId();
        for (Comment comment : comments) {
            if (comment.getUserId() == userId) {
                result = comment;
                comments.remove(comment);
            }
        }
        comments.add(0,result);
        return result;
    }
}
