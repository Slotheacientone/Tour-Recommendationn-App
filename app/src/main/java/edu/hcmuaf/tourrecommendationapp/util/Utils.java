package edu.hcmuaf.tourrecommendationapp.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.LoginResponse;

public class Utils {
    public static final String BASE_URL = Resource.getString(R.string.base_api_uri);
    private static final Gson gson = new Gson();
    private static final SharedPrefs sharePrefers = SharedPrefs.getInstance();

    public static String getAccessToken() {
        LoginResponse auth = sharePrefers.get("auth", LoginResponse.class);
        return auth == null ? null : auth.getAccessToken();
    }

    public static String getRefreshToken() {
        LoginResponse auth = sharePrefers.get("auth", LoginResponse.class);
        return auth == null ? null : auth.getRefreshToken();
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type typeOfT){
        return gson.fromJson(json, typeOfT);
    }

}
