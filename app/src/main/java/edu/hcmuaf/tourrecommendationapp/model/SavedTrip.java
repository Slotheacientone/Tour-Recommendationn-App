package edu.hcmuaf.tourrecommendationapp.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SavedTrip implements Serializable {
    private long userId;
    private long savedTripId;
    private List<Location> savedTripLocations;

    public String getSavedTripName() {
        String result = savedTripLocations.get(0).getLocationName();
        for (int i =1;i<savedTripLocations.size();i++) {
            result += " -> " + savedTripLocations.get(i).getLocationName();
        }
        return result;
    }
}
