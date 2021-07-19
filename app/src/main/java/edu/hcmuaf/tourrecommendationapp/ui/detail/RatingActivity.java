package edu.hcmuaf.tourrecommendationapp.ui.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.dto.CommentRequest;
import edu.hcmuaf.tourrecommendationapp.service.CommentService;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private long userId;
    private long locationId;
    private String comment;
    private CommentService commentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Bundle ratingBundle = getIntent().getExtras();
        commentEditText = findViewById(R.id.comment_edit_text);
        commentService = CommentService.getInstance();
        getSupportActionBar().setTitle("Rating");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(ratingBundle.getFloat("rating"));
        userId = ratingBundle.getLong("userId");
        locationId = ratingBundle.getLong("locationId");
        comment = ratingBundle.getString("comment");
        if (comment!=null){
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
                if (rate()) {
                    setResult(Activity.RESULT_OK);
                    finish();
                    return true;
                }else{
                    Toast.makeText(getBaseContext(),R.string.failed,Toast.LENGTH_LONG).show();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean rate() {
        boolean success = false;
        try {
            CommentRequest commentRequest = new CommentRequest(userId,
                    locationId, ratingBar.getRating(), commentEditText.getText().toString());
            success = commentService.registerComment(commentRequest);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}