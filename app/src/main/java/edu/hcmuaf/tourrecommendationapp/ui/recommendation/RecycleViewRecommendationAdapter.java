package edu.hcmuaf.tourrecommendationapp.ui.recommendation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class RecycleViewRecommendationAdapter extends RecyclerView.Adapter<RecycleViewRecommendationAdapter.RecycleViewRecommendationHolder> {

    private Context context;
    private List<Location> recommendations;

    public RecycleViewRecommendationAdapter(Context context, List<Location> recommendations) {
        this.context = context;
        this.recommendations = recommendations;
    }

    @NonNull
    @Override
    public RecycleViewRecommendationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        return new RecycleViewRecommendationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewRecommendationHolder holder, int position) {
        holder.locationName.setText(recommendations.get(position).getLocationName());
        holder.locationRatingBar.setRating(recommendations.get(position).getRatings());
        holder.locationNumberOfPeopleRating.setText(String.valueOf(recommendations.get(position).getNumberOfPeopleRating()));
        if (recommendations.get(position).getDistance() != -1 && recommendations.get(position).getDistance() != 0) {
            holder.locationDistance.setText("Khoảng cách: " + recommendations.get(position).getDistance());
        }
        holder.deleteButton.setVisibility(View.GONE);
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recommendations.get(holder.getAdapterPosition()).isSelected()) {
                    recommendations.get(holder.getAdapterPosition()).setSelected(true);
                    v.setBackgroundColor(Color.GRAY);
                } else {
                    recommendations.get(holder.getAdapterPosition()).setSelected(false);
                    v.setBackgroundColor(Color.WHITE);
                }
            }
        });
        holder.locationItemCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, LocationDetailActivity.class);
                intent.putExtra("location", recommendations.get(holder.getAdapterPosition()));
                context.startActivity(intent);
                return true;
            }
        });
        Picasso.get().load(recommendations.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);

    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }


    class RecycleViewRecommendationHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private ImageView locationImage;
        private RatingBar locationRatingBar;
        private TextView locationNumberOfPeopleRating;
        private ImageButton deleteButton;
        private TextView locationDistance;
        private CardView locationItemCardView;

        public RecycleViewRecommendationHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_item_name);
            locationImage = itemView.findViewById(R.id.location_item_image);
            locationRatingBar = itemView.findViewById(R.id.location_item_rating_bar);
            locationNumberOfPeopleRating = itemView.findViewById(R.id.location_item_number_of_people_rating);
            deleteButton = itemView.findViewById(R.id.delete_location_item_button);
            locationDistance = itemView.findViewById(R.id.location_distance);
            locationItemCardView = itemView.findViewById(R.id.location_item_card_view);
        }

    }
}
