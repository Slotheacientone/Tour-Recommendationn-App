package edu.hcmuaf.tourrecommendationapp.model;

import lombok.Data;

@Data
public class ApiResponse {
    private int code;
    private String body;
    private boolean isSuccessful;
}
