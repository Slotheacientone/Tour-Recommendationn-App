package edu.hcmuaf.tourrecommendationapp.ui.wishlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.WishlistService;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class RecycleViewWishlistAdapter extends RecyclerView.Adapter<RecycleViewWishlistAdapter.RecycleViewWishlistHolder> {

    private Context context;
    private List<Location> wishList;
    private User user;
    private WishlistService wishlistService;

    public RecycleViewWishlistAdapter(Context context, List<Location> wishList) {
        this.context = context;
        this.wishList = wishList;
        wishlistService = WishlistService.getInstance();
        user = SharedPrefs.getInstance().get("myInfo", User.class);
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
            @Override
            public void onClick(View v) {
                deleteLocationFromWishlist(user.getId(), wishList.get(holder.getAdapterPosition()).getLocationId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                                if (aBoolean) {
                                    wishList.remove(wishList.get(holder.getAdapterPosition()));
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.e(WishlistService.TAG, e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
            }
        });
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationDetailActivity.class);
                intent.putExtra("location", wishList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        Picasso.get().load(wishList.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);

    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public Observable<Boolean> deleteLocationFromWishlist(long userId, long locationId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                try {
                    boolean isSuccess = wishlistService.deleteLocationFromWishlist(userId, locationId);
                    emitter.onNext(isSuccess);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
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
