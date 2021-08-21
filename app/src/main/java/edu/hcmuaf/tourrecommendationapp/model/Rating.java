package edu.hcmuaf.tourrecommendationapp.model;

import java.util.Date;

import lombok.Data;

@Data
public class Rating {
    /** User id. */
    private long userId;

    private String userName;

    private String avatar;

    /** User rating. */
    private float rating;

    private String comment;

    private Date date;
}
