package edu.hcmuaf.tourrecommendationapp.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
