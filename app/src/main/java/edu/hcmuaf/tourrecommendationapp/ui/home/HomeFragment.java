package edu.hcmuaf.tourrecommendationapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Location;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;

public class HomeFragment extends Fragment {

    private Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationDetailActivity.class);
                Location location = new Location();
                location.setLocationId(957);
                location.setLocationName("Đồi cát trắng");
                location.setLocationImageUrl("https://media-cdn.tripadvisor.com/media/photo-o/12/f0/22/ff/img-20180512-213049-591.jpg");
                location.setRatings(4);
                location.setNumberOfPeopleRating(81);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });
    }
}