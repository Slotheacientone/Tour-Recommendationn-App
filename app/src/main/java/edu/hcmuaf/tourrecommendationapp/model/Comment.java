package edu.hcmuaf.tourrecommendationapp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Setter
@Getter
public class Comment {
    /** User id. */
    private long userId;

    private String userName;

    private String avatar;

    /** User rating. */
    private float rating;

    private String comment;

    private Date date;
}
