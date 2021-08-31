package edu.hcmuaf.tourrecommendationapp.model;

import lombok.Data;

@Data
public class LocationDetail {
    private String name;
    private String address;
    private String phoneNumber;
    private boolean openNow;
    private float rating;
    private int userRatingTotal;
}
