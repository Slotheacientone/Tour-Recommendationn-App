package edu.hcmuaf.tourrecommendationapp.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.model.Location;

public class SortService {
    private static SortService mInstance;

    private SortService() {
    }

    public static SortService getInstance() {
        if (mInstance == null)
            mInstance = new SortService();
        return mInstance;
    }

    public void sortByDistance(List<Location> locations) {
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location location1, Location location2) {
                return location1.getDistance() < location2.getDistance() ? -1 : 1;
            }
        });
    }


    public void sortByOrder(List<Location> wishlist) {
        Collections.sort(wishlist, new Comparator<Location>() {
            @Override
            public int compare(Location location1, Location location2) {
                return location1.getOrder() < location2.getOrder() ? -1 : 1;
            }
        });
    }
}
