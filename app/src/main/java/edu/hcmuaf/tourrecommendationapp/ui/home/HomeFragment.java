package edu.hcmuaf.tourrecommendationapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.ui.detail.LocationDetailActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        button = root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationDetailActivity.class);
                intent.putExtra("locationId", (long) 957);
                intent.putExtra("locationName", "Đồi cát trắng");
                intent.putExtra("locationImage", "https://media-cdn.tripadvisor.com/media/photo-o/12/f0/22/ff/img-20180512-213049-591.jpg");
                intent.putExtra("rating", (float) 4);
                intent.putExtra("numberOfPeopleRating", 3265);
                startActivity(intent);
            }
        });
        return root;
    }
}