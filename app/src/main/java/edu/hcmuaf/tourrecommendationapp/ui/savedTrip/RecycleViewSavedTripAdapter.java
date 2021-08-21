package edu.hcmuaf.tourrecommendationapp.ui.savedTrip;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.SavedTrip;
import edu.hcmuaf.tourrecommendationapp.model.User;
import edu.hcmuaf.tourrecommendationapp.service.SavedTripService;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import lombok.SneakyThrows;

public class RecycleViewSavedTripAdapter extends RecyclerView.Adapter<RecycleViewSavedTripAdapter.RecycleViewSavedTripHolder> {

    private Context context;
    private List<SavedTrip> savedTripList;
    private SavedTripService savedTripService;
    private User user;

    public RecycleViewSavedTripAdapter(Context context) {
        this.context = context;
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        user = SharedPrefs.getInstance().get("myInfo", User.class);
        savedTripService = SavedTripService.getInstance();
        try {
            savedTripList = savedTripService.getSavedTriplist(user.getId());
            System.out.println(savedTripList);
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
    public RecycleViewSavedTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.saved_trip_item, parent, false);
        return new RecycleViewSavedTripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewSavedTripHolder holder, int position) {
        holder.savedTripName.setText(savedTripList.get(position).getSavedTripName());
        holder.savedTripButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                savedTripService.deleteSavedTrip(savedTripList.get(holder.getAdapterPosition()).getSavedTripId());
                savedTripList = savedTripService.getSavedTriplist(user.getId());
                notifyDataChanged();
            }
        });
        holder.savedTripCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SavedTripDetailActivity.class);
                intent.putExtra("savedTrip",savedTripList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

    }
    public void notifyDataChanged(){
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return savedTripList.size();
    }


    class RecycleViewSavedTripHolder extends RecyclerView.ViewHolder {

        private TextView savedTripName;
        private ImageButton savedTripButton;
        private CardView savedTripCardView;

        public RecycleViewSavedTripHolder(@NonNull View itemView) {
            super(itemView);
            savedTripName = itemView.findViewById(R.id.saved_trip_name);
            savedTripButton = itemView.findViewById(R.id.delete_saved_trip_button);
            savedTripCardView = itemView.findViewById(R.id.saved_trip_card_view);
        }

    }
}
