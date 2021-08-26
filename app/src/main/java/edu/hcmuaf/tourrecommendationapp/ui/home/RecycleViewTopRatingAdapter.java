package edu.hcmuaf.tourrecommendationapp.ui.home;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;

import edu.hcmuaf.tourrecommendationapp.service.LocationService;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class RecycleViewTopRatingAdapter extends RecyclerView.Adapter<RecycleViewTopRatingAdapter.RecycleViewTopRatingHolder> implements Filterable {

    private Context context;
    private List<Location> locations;
    private List<Location> arrayList;
    private LocationService locationService = LocationService.getInstance();

    public RecycleViewTopRatingAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(locations);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public RecycleViewTopRatingAdapter(Context context) {
        this.context = context;
        this.locations = new ArrayList<>();
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(locations);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @NonNull
    @NotNull
    @Override
    public RecycleViewTopRatingHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        return new RecycleViewTopRatingAdapter.RecycleViewTopRatingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecycleViewTopRatingHolder holder, int position) {
        holder.locationName.setText(locations.get(position).getLocationName());
        holder.locationRatingBar.setRating(locations.get(position).getRatings());
        holder.locationNumberOfPeopleRating.setText(String.valueOf(locations.get(position).getNumberOfPeopleRating()));
        holder.deleteButton.setVisibility(View.GONE);
        holder.locationItemCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LocationDetailActivity.class);
            intent.putExtra("location", locations.get(holder.getAdapterPosition()));
            context.startActivity(intent);
        });

        Picasso.get().load(locations.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }


    @Override
    public Filter getFilter() {


        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();
                locations.clear();
                if (query.isEmpty()) {
                    locations.addAll(arrayList);
                } else {
                    try {
                        arrayList = locationService.filter(query);
                        for (Location location : arrayList) {
                            if (location.getLocationName().toLowerCase(Locale.getDefault()).contains(query)) {
                                locations.add(location);
                            }
                        }
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                FilterResults results = new FilterResults();
                results.values = locations;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    class RecycleViewTopRatingHolder extends RecyclerView.ViewHolder {

        private final TextView locationName;
        private final ImageView locationImage;
        private final RatingBar locationRatingBar;
        private final TextView locationNumberOfPeopleRating;
        private final ImageButton deleteButton;
        private final CardView locationItemCardView;

        public RecycleViewTopRatingHolder(@NonNull View itemView) {
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
