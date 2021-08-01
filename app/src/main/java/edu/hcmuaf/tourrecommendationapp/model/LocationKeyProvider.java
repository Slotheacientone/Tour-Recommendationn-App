package edu.hcmuaf.tourrecommendationapp.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.List;

public class LocationKeyProvider extends ItemKeyProvider {

    private final List<Location> locations;

    public LocationKeyProvider(int scope, List<Location> locations) {
        super(scope);
        this.locations = locations;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return locations.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return 0;
    }
}
