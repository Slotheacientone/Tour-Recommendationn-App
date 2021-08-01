package edu.hcmuaf.tourrecommendationapp.ui.wishlist;

import android.content.Context;
import android.content.Intent;
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
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import lombok.SneakyThrows;

public class RecycleViewWishlistAdapter extends RecyclerView.Adapter<RecycleViewWishlistAdapter.RecycleViewWishlistHolder> {

    private Context context;
    private List<Location> wishList;
    private WishlistService wishlistService;
    private User user;

    public RecycleViewWishlistAdapter(Context context) {
        this.context = context;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        wishlistService = WishlistService.getInstance();
        try {
            wishList = wishlistService.getWishlist(user.getId());
            System.out.println(wishList);
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
    public RecycleViewWishlistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        return new RecycleViewWishlistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewWishlistHolder holder, int position) {
        holder.locationName.setText(wishList.get(position).getLocationName());
        holder.locationRatingBar.setRating(wishList.get(position).getRatings());
        holder.locationNumberOfPeopleRating.setText(String.valueOf(wishList.get(position).getNumberOfPeopleRating()));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                wishlistService.deleteLocationFromWishlist(user.getId(),wishList.get(holder.getAdapterPosition()).getLocationId());
                wishList = wishlistService.getWishlist(user.getId());
                notifyDataChanged();
            }
        });
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationDetailActivity.class);
                intent.putExtra("location",wishList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        Picasso.get().load(wishList.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);

    }
    public void notifyDataChanged(){
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }


    class RecycleViewWishlistHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private ImageView locationImage;
        private RatingBar locationRatingBar;
        private TextView locationNumberOfPeopleRating;
        private ImageButton deleteButton;
        private CardView locationItemCardView;

        public RecycleViewWishlistHolder(@NonNull View itemView) {
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
