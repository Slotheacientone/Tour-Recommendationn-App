package edu.hcmuaf.tourrecommendationapp.ui.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.hcmuaf.tourrecommendationapp.R;
import edu.hcmuaf.tourrecommendationapp.model.Comment;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RecycleViewCommentAdapter extends RecyclerView.Adapter<RecycleViewCommentAdapter.RecycleViewCommentHolder> {

    private Context context;
    private List<Comment> comments;
    DateFormat dateFormat;

    public RecycleViewCommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @NonNull
    @Override
    public RecycleViewCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.comment_item, parent, false);
        return new RecycleViewCommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewCommentHolder holder, int position) {
        holder.name.setText(comments.get(position).getUserName());
        holder.ratingBar.setRating(comments.get(position).getRating());
        holder.comment.setText(comments.get(position).getComment());
        holder.date.setText(dateFormat.format(comments.get(position).getDate()));
        Picasso.get().load(comments.get(position).getAvatar()).transform(new CropCircleTransformation()).into(holder.avatar);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class RecycleViewCommentHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView avatar;
        private RatingBar ratingBar;
        private TextView comment;
        private TextView date;

        public RecycleViewCommentHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment_name);
            avatar = itemView.findViewById(R.id.comment_item_avatar);
            ratingBar = itemView.findViewById(R.id.item_rating_bar);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.comment_date);
        }

    }
}
