package edu.hcmuaf.tourrecommendationapp.util;

import com.google.gson.Gson;

public class Utils {

    public static final String BASE_URL = "http://192.168.222.101:8080";
    private static final Gson gson = new Gson();

    public static String getAccessToken() {
        return SharedPrefs.getInstance().get("accessToken", String.class);
    }

    public static String getRefreshToken() {
        return SharedPrefs.getInstance().get("refreshToken", String.class);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
