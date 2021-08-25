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

public class RecycleViewWishlistRecommendationAdapter extends RecyclerView.Adapter<RecycleViewWishlistRecommendationAdapter.RecycleViewWishlistRecommedationHolder> {

    private Context context;
    private List<Location> wishlist;


    public RecycleViewWishlistRecommendationAdapter(Context context, List<Location> wishlist) {
        this.context = context;
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public RecycleViewWishlistRecommedationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        return new RecycleViewWishlistRecommedationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewWishlistRecommedationHolder holder, int position) {
        holder.locationName.setText(wishlist.get(position).getLocationName());
        holder.locationRatingBar.setRating(wishlist.get(position).getRatings());
        holder.locationNumberOfPeopleRating.setText(String.valueOf(wishlist.get(position).getNumberOfPeopleRating()));
        holder.deleteButton.setVisibility(View.GONE);
        if (wishlist.get(position).getDistance() != -1 && wishlist.get(position).getDistance() != 0) {
            holder.locationDistance.setText("Khoảng cách: " + wishlist.get(position).getDistance() + " km");
        }
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wishlist.get(holder.getAdapterPosition()).isSelected()) {
                    wishlist.get(holder.getAdapterPosition()).setSelected(true);
                    v.setBackgroundColor(Color.GRAY);
                } else {
                    wishlist.get(holder.getAdapterPosition()).setSelected(false);
                    v.setBackgroundColor(Color.WHITE);
                }
            }
        });
        holder.locationItemCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, LocationDetailActivity.class);
                intent.putExtra("location", wishlist.get(holder.getAdapterPosition()));
                context.startActivity(intent);
                return true;
            }
        });
        Picasso.get().load(wishlist.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);

    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }


    class RecycleViewWishlistRecommedationHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private ImageView locationImage;
        private RatingBar locationRatingBar;
        private TextView locationNumberOfPeopleRating;
        private ImageButton deleteButton;
        private TextView locationDistance;
        private CardView locationItemCardView;

        public RecycleViewWishlistRecommedationHolder(@NonNull View itemView) {
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
