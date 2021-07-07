package edu.hcmuaf.tourrecommendationapp.util;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Utils {

    public static final String BASE_URL = "http://192.168.1.22:8080";
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

    public static <T> T fromJson(String json, Type typeOfT){
        return gson.fromJson(json, typeOfT);
    }

}
