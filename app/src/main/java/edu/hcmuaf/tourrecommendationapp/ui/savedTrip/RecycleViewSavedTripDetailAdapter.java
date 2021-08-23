package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

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
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import lombok.SneakyThrows;

public class RecycleViewSavedTripDetailAdapter extends RecyclerView.Adapter<RecycleViewSavedTripDetailAdapter.RecycleViewSavedTripDetailHolder> {

    private Context context;
    private List<Location> savedTripLocations;
    private SavedTripService savedTripService;
    private long savedTripId;

    public RecycleViewSavedTripDetailAdapter(Context context, List<Location> savedTripLocations, long savedTripId) {
        this.context = context;
        this.savedTripLocations = savedTripLocations;
        this.savedTripId = savedTripId;
        savedTripService = SavedTripService.getInstance();
    }

    @NonNull
    @Override
    public RecycleViewSavedTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        return new RecycleViewSavedTripDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewSavedTripDetailHolder holder, int position) {
        holder.locationName.setText(savedTripLocations.get(position).getLocationName());
        holder.locationRatingBar.setRating(savedTripLocations.get(position).getRatings());
        holder.locationNumberOfPeopleRating.setText(String.valueOf(savedTripLocations.get(position).getNumberOfPeopleRating()));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                deleteLocationFromSavedTrip(savedTripId, savedTripLocations.get(holder.getAdapterPosition()).getLocationId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                                if (aBoolean) {
                                    savedTripLocations.remove(savedTripLocations.get(holder.getAdapterPosition()));
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.e(SavedTripService.TAG, e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                notifyDataSetChanged();
                            }
                        });
            }
        });
        holder.locationItemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationDetailActivity.class);
                intent.putExtra("location", savedTripLocations.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        Picasso.get().load(savedTripLocations.get(position).getLocationImageUrl()).transform(new CropSquareTransformation()).into(holder.locationImage);

    }

    @Override
    public int getItemCount() {
        return savedTripLocations.size();
    }

    public Observable<Boolean> deleteLocationFromSavedTrip(long savedTripId, long locationId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) {
                try {
                    boolean isSuccess = savedTripService.deleteLocationFromSavedTrip(savedTripId, locationId);
                    emitter.onNext(isSuccess);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


    class RecycleViewSavedTripDetailHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private ImageView locationImage;
        private RatingBar locationRatingBar;
        private TextView locationNumberOfPeopleRating;
        private ImageButton deleteButton;
        private CardView locationItemCardView;

        public RecycleViewSavedTripDetailHolder(@NonNull View itemView) {
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
