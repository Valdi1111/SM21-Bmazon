package org.valdi.bmazon.fragments.product;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import org.valdi.bmazon.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    private final ShapeableImageView reviewerAvatar;
    private final TextView reviewerUsername;
    private final TextView date;
    private final AppCompatRatingBar rating;
    private final TextView title;
    private final TextView description;
    private final ImageView image;
    private final Button helpful;
    private final TextView votes;

    public ReviewViewHolder(@NonNull View view) {
        super(view);
        this.reviewerAvatar = view.findViewById(R.id.review_reviewer_avatar);
        this.reviewerUsername = view.findViewById(R.id.review_reviewer_username);
        this.date = view.findViewById(R.id.review_date);
        this.rating = view.findViewById(R.id.review_rating);
        this.title = view.findViewById(R.id.review_title);
        this.description = view.findViewById(R.id.review_description);
        this.image = view.findViewById(R.id.review_image);
        this.helpful = view.findViewById(R.id.review_helpful);
        this.votes = view.findViewById(R.id.review_votes);
    }

    public ShapeableImageView getReviewerAvatar() {
        return reviewerAvatar;
    }

    public TextView getReviewerUsername() {
        return reviewerUsername;
    }

    public TextView getDate() {
        return date;
    }

    public AppCompatRatingBar getRating() {
        return rating;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDescription() {
        return description;
    }

    public ImageView getImage() {
        return image;
    }

    public Button getHelpful() {
        return helpful;
    }

    public TextView getVotes() {
        return votes;
    }
}
