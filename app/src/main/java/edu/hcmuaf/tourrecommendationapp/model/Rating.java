package edu.hcmuaf.tourrecommendationapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Data;

@Data
public class Rating {
    /** User id. */
    private long userId;

    private String userName;

    @SerializedName("userImageUrl")
    private String avatar;

    /** User rating. */
    private float rating;

    private String comment;

    private Date date;
}
