package edu.hcmuaf.tourrecommendationapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    private long userId;
    private long locationId;
    private float rating;
    private String comment;
}
