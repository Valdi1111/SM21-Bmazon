package org.valdi.bmazon.fragments.notifications;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.Notification;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NotificationAdapter extends AbstractRecyclerViewAdapter<Notification, NotificationViewHolder> {
    private static final String TAG = NotificationAdapter.class.getSimpleName();
    private final CompositeSubscription subscriptions;

    public NotificationAdapter(final Context context, final CompositeSubscription subscriptions) {
        super(context, R.layout.notification);
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    protected NotificationViewHolder createFromView(@NonNull View view) {
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        final Notification n = getItems().get(position);
        holder.getTitle().setText(n.getTitle());
        holder.getDescription().setText(n.getDescription());
        holder.getDate().setText(Util.formatDate(n.getCreated(), Util.DATE_TIME_FORMAT));
        holder.getRead().setOnClickListener(view -> {
            this.subscriptions.add(NetworkUtil
                    .getRetrofit((AuthProvider) getContext())
                    .postNotificationRead(n.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            unused -> {
                                Toast.makeText(getContext(), R.string.notification_mark_read, Toast.LENGTH_SHORT).show();
                                delete(position);
                            },
                            this::handleError
                    )
            );
        });
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(getContext(), TAG, error);
    }

}
