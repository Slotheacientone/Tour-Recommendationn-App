package edu.hcmuaf.tourrecommendationapp.ui.locationDetail;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.RatingRequest;
import edu.hcmuaf.tourrecommendationapp.service.RatingService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private long userId;
    private long locationId;
    private String comment;
    private RatingService ratingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Bundle ratingBundle = getIntent().getExtras();
        commentEditText = findViewById(R.id.comment_edit_text);
        ratingService = RatingService.getInstance();
        getSupportActionBar().setTitle("Rating");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(ratingBundle.getFloat("rating"));
        userId = ratingBundle.getLong("userId");
        locationId = ratingBundle.getLong("locationId");
        comment = ratingBundle.getString("comment");
        if (comment != null) {
            commentEditText.setText(comment);
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rating_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.rate_menu_item:
                RatingRequest ratingRequest = new RatingRequest(userId,
                        locationId, ratingBar.getRating(), commentEditText.getText().toString());
                registerRating(ratingRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@NonNull Boolean aBoolean) {
                                if (aBoolean) {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), R.string.failed, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.e(RatingService.TAG, e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Observable<Boolean> registerRating(RatingRequest ratingRequest) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                try {
                    boolean isSuccess = ratingService.registerRating(ratingRequest);
                    emitter.onNext(isSuccess);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

}