package edu.hcmuaf.tourrecommendationapp.ui.recommendation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.RecommendateService;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import lombok.SneakyThrows;

public class RecycleViewRecommendationAdapter extends RecyclerView.Adapter<RecycleViewRecommendationAdapter.RecycleViewRecommendationHolder> {

    private Context context;
    private List<Location> recommendations;
    private RecommendateService recommendateService;
    private User user;
    private List<Location> selectedLocations;

    public RecycleViewRecommendationAdapter(Context context, List<Location> selectedLocations) {
        this.context = context;
        this.selectedLocations = selectedLocations;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        recommendateService = RecommendateService.getInstance();
        try {
            recommendations = recommendateService.getRecommendations(user.getId());
            System.out.println(recommendations);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        holder.deleteButton.setVisibility(View.GONE);
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedLocations.contains(recommendations.get(holder.getAdapterPosition()))){
                    selectedLocations.remove(recommendations.get(holder.getAdapterPosition()));
                    v.setBackgroundColor(Color.GRAY);
                }else {
                    selectedLocations.add(recommendations.get(holder.getAdapterPosition()));
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
        private CardView locationItemCardView;

        public RecycleViewRecommendationHolder(@NonNull View itemView) {
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
