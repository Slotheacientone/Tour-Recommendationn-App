package edu.hcmuaf.tourrecommendationapp.util;

import android.content.res.Resources;

public class Resource{

    public static String getString(int id){
        return App.self().getResources().getString(id);
    }
}
