package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

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
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class RecycleViewSimilarLocationsAdapter extends RecyclerView.Adapter<RecycleViewSimilarLocationsAdapter.RecycleViewSimilarLocationHolder> {

    private Context context;
    private List<Location> similarLocations;

    public RecycleViewSimilarLocationsAdapter(Context context, List<Location> similarLocations) {
        this.context = context;
        this.similarLocations = similarLocations;
    }

    @NonNull
    @Override
    public RecycleViewSimilarLocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        return new RecycleViewSimilarLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewSimilarLocationHolder holder, int position) {
        holder.locationName.setText(similarLocations.get(position).getLocationName());
        holder.locationRatingBar.setRating(similarLocations.get(position).getRatings());
        holder.locationNumberOfPeopleRating.setText(String.valueOf(similarLocations.get(position).getNumberOfPeopleRating()));
        holder.deleteButton.setVisibility(View.GONE);
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationDetailActivity.class);
                intent.putExtra("location", similarLocations.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        Picasso.get().load(similarLocations.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);

    }

    @Override
    public int getItemCount() {
        return similarLocations.size();
    }


    class RecycleViewSimilarLocationHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private ImageView locationImage;
        private RatingBar locationRatingBar;
        private TextView locationNumberOfPeopleRating;
        private ImageButton deleteButton;
        private CardView locationItemCardView;

        public RecycleViewSimilarLocationHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_item_name);
            locationImage = itemView.findViewById(R.id.location_item_image);
            locationRatingBar = itemView.findViewById(R.id.location_item_rating_bar);
            locationNumberOfPeopleRating = itemView.findViewById(R.id.location_item_number_of_people_rating);
            deleteButton = itemView.findViewById(R.id.delete_location_item_button);
            locationItemCardView = itemView.findViewById(R.id.location_item_card_view);
        }

    }
}
