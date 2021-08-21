package edu.hcmuaf.tourrecommendationapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Route {
    private List<LatLng> polyline;

    private LatLngBounds bounds;

    public Route(){
        polyline = new ArrayList<>();
    }

    public void addAllPolyline(List<LatLng> polyline){
        this.polyline.addAll(polyline);
    }

}
