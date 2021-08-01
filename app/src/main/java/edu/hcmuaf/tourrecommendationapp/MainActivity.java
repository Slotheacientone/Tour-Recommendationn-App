package edu.hcmuaf.tourrecommendationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuView;

import edu.hcmuaf.tourrecommendationapp.dto.LoginResponse;
import edu.hcmuaf.tourrecommendationapp.service.AuthService;
import edu.hcmuaf.tourrecommendationapp.ui.locationDetail.LocationDetailActivity;
import edu.hcmuaf.tourrecommendationapp.ui.recommendation.RecommendActivity;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FloatingActionButton recommedButton;
    private AuthService authService = AuthService.getInstance();
    private SharedPrefs sharedPrefs = SharedPrefs.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recommedButton = findViewById(R.id.recommend_button);
        recommedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RecommendActivity.class);
                startActivity(intent);
            }
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_wishlist, R.id.navigation_saved_trip, R.id.navigation_dashboard)
                .build();
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_wishlist, R.id.navigation_saved_trip, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (sharedPrefs.get("auth", LoginResponse.class) == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}