package org.valdi.bmazon.fragments.product;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.product.ProductFaq;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FaqAdapter extends AbstractRecyclerViewAdapter<ProductFaq, FaqViewHolder> {
    private static final String TAG = FaqAdapter.class.getSimpleName();
    private final CompositeSubscription subscriptions;

    public FaqAdapter(final Context context, final CompositeSubscription subscriptions) {
        super(context, R.layout.product_faq);
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    protected FaqViewHolder createFromView(@NonNull View view) {
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        final ProductFaq f = getItems().get(position);
        holder.getQuestion().setText(f.getQuestion());
        holder.getAnswer().setText(f.getAnswer());
        holder.getVotes().setText(String.valueOf(f.getUpvotes()));
        holder.getVoteUp().setOnClickListener(view -> Util.showNotLoggedIn(getContext()));
        holder.getVoteDown().setOnClickListener(view -> Util.showNotLoggedIn(getContext()));
        holder.getVoteUp().setTag(f.getId());
        holder.getVoteDown().setTag(f.getId());
        if (Util.isLogged(getContext())) {
            this.subscriptions.add(NetworkUtil
                    .getRetrofit((AuthProvider) getContext())
                    .getFaqUpvote(f.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            response -> {
                                final int val = response.get("upvote").getAsInt();
                                holder.getVoteUp().setOnClickListener(view -> onUpVote(holder));
                                holder.getVoteDown().setOnClickListener(view -> onDownVote(holder));
                                holder.setValue(val);
                            },
                            this::handleError
                    )
            );
        }
    }

    private void onUpVote(final FaqViewHolder holder) {
        updateVote(holder, (int) holder.getVoteDown().getTag(), holder.getValue() == 1 ? 0 : 1);
    }

    private void onDownVote(final FaqViewHolder holder) {
        updateVote(holder, (int) holder.getVoteDown().getTag(), holder.getValue() == -1 ? 0 : -1);
    }

    private void updateVote(final FaqViewHolder holder, final int id, final int val) {
        final JsonObject json = new JsonObject();
        json.addProperty("vote", val);
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) getContext())
                .postFaqUpvote(id, json)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> holder.setValue(val), this::handleError)
        );
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(getContext(), TAG, error);
    }
}
