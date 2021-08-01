package edu.hcmuaf.tourrecommendationapp.model;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Location implements Serializable {
    /**
     * Location id.
     */
    private long locationId;
    private String locationName;
    private String locationImageUrl;
    private float ratings;
    private int numberOfPeopleRating;
    /**
     * Location latitude.
     */
    private double locationLatitude;

    /**
     * Location longtitude.
     */
    private double locationLongtitude;

    private double distance;
}
