package edu.hcmuaf.tourrecommendationapp.dto;

import java.util.List;

import edu.hcmuaf.tourrecommendationapp.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceRequest {
    private double latitude;
    private double longitude;
    private List<Location> locations;
}
