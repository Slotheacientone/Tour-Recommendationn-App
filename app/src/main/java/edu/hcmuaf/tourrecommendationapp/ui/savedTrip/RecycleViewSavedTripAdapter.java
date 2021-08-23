package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import lombok.SneakyThrows;

public class RecycleViewSavedTripAdapter extends RecyclerView.Adapter<RecycleViewSavedTripAdapter.RecycleViewSavedTripHolder> {

    private Context context;
    private List<SavedTrip> savedTrips;
    private SavedTripService savedTripService;

    public RecycleViewSavedTripAdapter(Context context, List<SavedTrip> savedTrips) {
        this.context = context;
        savedTripService = SavedTripService.getInstance();
        this.savedTrips = savedTrips;
    }

    @NonNull
    @Override
    public RecycleViewSavedTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.saved_trip_item, parent, false);
        return new RecycleViewSavedTripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewSavedTripHolder holder, int position) {
        holder.savedTripName.setText(savedTrips.get(position).getSavedTripName());
        holder.savedTripButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                deleteSavedTrip(savedTrips.get(holder.getAdapterPosition()).getSavedTripId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                                if (aBoolean) {
                                    savedTrips.remove(savedTrips.get(holder.getAdapterPosition()));
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
        holder.savedTripCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SavedTripDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("savedTripId",savedTrips.get(holder.getAdapterPosition()).getSavedTripId());
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);
            }
        });
        Picasso.get().load(savedTrips.get(position).getSavedTripLocations().get(0).getLocationImageUrl())
                .transform(new CropSquareTransformation()).into(holder.savedTripImage);
    }

    public Observable<Boolean> deleteSavedTrip(long savedTripId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter){
                try {
                    boolean isSuccess = savedTripService.deleteSavedTrip(savedTripId);
                    emitter.onNext(isSuccess);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


    @Override
    public int getItemCount() {

        System.out.println(savedTrips.size());return savedTrips.size();
    }


    class RecycleViewSavedTripHolder extends RecyclerView.ViewHolder {

        private TextView savedTripName;
        private ImageView savedTripImage;
        private ImageButton savedTripButton;
        private CardView savedTripCardView;

        public RecycleViewSavedTripHolder(@NonNull View itemView) {
            super(itemView);
            savedTripName = itemView.findViewById(R.id.saved_trip_name);
            savedTripImage = itemView.findViewById(R.id.saved_trip_item_image);
            savedTripButton = itemView.findViewById(R.id.delete_saved_trip_button);
            savedTripCardView = itemView.findViewById(R.id.saved_trip_card_view);
        }

    }
}
