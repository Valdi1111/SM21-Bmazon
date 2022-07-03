package org.valdi.bmazon.fragments.product;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.product.ProductReview;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ReviewAdapter extends AbstractRecyclerViewAdapter<ProductReview, ReviewViewHolder> {
    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private final CompositeSubscription subscriptions;

    public ReviewAdapter(final Context context, final CompositeSubscription subscriptions) {
        super(context, R.layout.product_review);
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    protected ReviewViewHolder createFromView(@NonNull View view) {
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        final ProductReview r = getItems().get(position);
        Util.setAvatar(holder.getReviewerAvatar(), r.getAvatar());
        holder.getReviewerUsername().setText(r.getUsername());
        holder.getDate().setText(getContext().getString(R.string.review_date, Util.formatDate(r.getCreated(), Util.DATE_FORMAT)));
        holder.getRating().setRating(r.getRating());
        holder.getTitle().setText(r.getTitle());
        holder.getDescription().setText(r.getDescription());
        Util.setReviewImage(holder.getImage(), r.getImage());
        holder.getVotes().setText(getContext().getString(R.string.review_votes, r.getUpvotes()));
        holder.getHelpful().setOnClickListener(view -> Util.showNotLoggedIn(getContext()));
        holder.getHelpful().setTag(r.getId());
        if (Util.isLogged(getContext())) {
            this.subscriptions.add(NetworkUtil
                    .getRetrofit((AuthProvider) getContext())
                    .getReviewHelpful(r.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            response -> {
                                final int val = response.get("helpful").getAsInt();
                                changeButton(holder.getHelpful(), val == 1);
                            },
                            this::handleError
                    )
            );
        }
    }

    private void changeButton(final Button button, final boolean helpful) {
        if (helpful) {
            button.setText(R.string.review_undo);
            button.setOnClickListener(this::onUndo);
        } else {
            button.setText(R.string.review_helpful);
            button.setOnClickListener(this::onHelpful);
        }
    }

    private void onHelpful(final View view) {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) getContext())
                .postReviewHelpful((int) view.getTag())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> changeButton((Button) view, true), this::handleError)
        );
    }

    private void onUndo(final View view) {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) getContext())
                .deleteReviewHelpful((int) view.getTag())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> changeButton((Button) view, false), this::handleError)
        );
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(getContext(), TAG, error);
    }

}
