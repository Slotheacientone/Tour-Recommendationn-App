package edu.hcmuaf.tourrecommendationapp.util;

public class Resource{

    public static String getString(int id){
        return App.self().getResources().getString(id);
    }
}
