package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Rating;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RecycleViewRatingAdapter extends RecyclerView.Adapter<RecycleViewRatingAdapter.RecycleViewCommentHolder> {

    private Context context;
    private List<Rating> ratings;
    DateFormat dateFormat;

    public RecycleViewRatingAdapter(Context context, List<Rating> ratings) {
        this.context = context;
        this.ratings = ratings;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @NonNull
    @Override
    public RecycleViewCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.rating_item, parent, false);
        return new RecycleViewCommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewCommentHolder holder, int position) {
        holder.name.setText(ratings.get(position).getUserName());
        holder.ratingBar.setRating(ratings.get(position).getRating());
        holder.comment.setText(ratings.get(position).getComment());
        holder.date.setText(dateFormat.format(ratings.get(position).getDate()));
        Picasso.get().load(ratings.get(position).getAvatar()).transform(new CropCircleTransformation()).into(holder.avatar);

    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    class RecycleViewCommentHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView avatar;
        private RatingBar ratingBar;
        private TextView comment;
        private TextView date;

        public RecycleViewCommentHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rating_item_user_name);
            avatar = itemView.findViewById(R.id.rating_item_user_avatar);
            ratingBar = itemView.findViewById(R.id.rating_item_rating_bar);
            comment = itemView.findViewById(R.id.rating_item_comment);
            date = itemView.findViewById(R.id.rating_item_date);
        }

    }
}
