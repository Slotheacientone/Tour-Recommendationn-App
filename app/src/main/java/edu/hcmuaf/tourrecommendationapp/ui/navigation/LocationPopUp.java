package edu.hcmuaf.tourrecommendationapp.ui.navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.LocationDetail;
import edu.hcmuaf.tourrecommendationapp.service.NearbyPlacesAPIService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocationPopUp {
    private String placeId;
    private NearbyPlacesAPIService nearbyPlacesAPIService;
    private TextView locationName;
    private RatingBar ratingBar;
    private TextView isOpen;
    private TextView address;
    private TextView phoneNumber;
    private TextView userRatingTotal;
    private Button closeButton;

    public LocationPopUp(String placeId) {
        this.placeId = placeId;
        nearbyPlacesAPIService = NearbyPlacesAPIService.getInstance();
    }

    public void showPopupWindow(final View view) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.location_pop_up, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        closeButton = popupView.findViewById(R.id.closeButton);
        locationName = popupView.findViewById(R.id.pop_up_location_name);
        ratingBar = popupView.findViewById(R.id.pop_up_location_rating_bar);
        isOpen = popupView.findViewById(R.id.pop_up_open);
        address = popupView.findViewById(R.id.pop_up_address);
        phoneNumber = popupView.findViewById(R.id.pop_up_phone_number);
        userRatingTotal = popupView.findViewById(R.id.pop_up_location_number_of_people_rating);

        getLocationDetail(this.placeId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LocationDetail>() {
                    @Override
                    public void onNext(@NonNull LocationDetail locationDetail) {
                        locationName.setText(locationDetail.getName());
                        ratingBar.setRating(locationDetail.getRating());
                        if(locationDetail.getAddress()!=null) {
                            address.setText("Address: " + locationDetail.getAddress());
                        }else{
                            address.setVisibility(View.GONE);
                        }
                        if(locationDetail.getPhoneNumber()!=null) {
                            phoneNumber.setText("Phone number: " + locationDetail.getPhoneNumber());
                        }else{
                            address.setVisibility(View.GONE);
                        }
                        userRatingTotal.setText(String.valueOf(locationDetail.getUserRatingTotal()));
                        if(locationDetail.isOpenNow()){
                            isOpen.setText("Opening");
                        }else{
                            isOpen.setText("Closed");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(NearbyPlacesAPIService.TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public Observable<LocationDetail> getLocationDetail(String placeId) {
        return Observable.create(new ObservableOnSubscribe<LocationDetail>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LocationDetail> emitter) {
                try {
                    LocationDetail locationDetail = nearbyPlacesAPIService.getLocationDetail(placeId);
                    emitter.onNext(locationDetail);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
