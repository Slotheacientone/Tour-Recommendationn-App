package edu.hcmuaf.tourrecommendationapp.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Location implements Serializable {
    /**
     * Location id.
     */
    private long locationId;
    private String locationName;
    private String locationImageUrl;
    private float ratings;
    private float recommendScore;
    private int numberOfPeopleRating;
    /**
     * Location latitude.
     */
    private double locationLatitude;

    /**
     * Location longtitude.
     */
    private double locationLongitude;

    private long distance;

    private int wishListOrder;

    private boolean isSelected=false;
}
