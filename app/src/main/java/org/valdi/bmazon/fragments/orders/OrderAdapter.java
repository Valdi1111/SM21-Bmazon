package org.valdi.bmazon.fragments.orders;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.order.Order;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OrderAdapter extends AbstractRecyclerViewAdapter<Order, OrderViewHolder> {
    private static final String TAG = OrderAdapter.class.getSimpleName();
    private final CompositeSubscription subscriptions;

    public OrderAdapter(final Context context, final CompositeSubscription subscriptions) {
        super(context, R.layout.order);
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    protected OrderViewHolder createFromView(@NonNull View view) {
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final Order o = getItems().get(position);
        holder.getShipment().setText(
                getContext().getString(
                        R.string.order_shipment,
                        o.getShipmentStreet(),
                        o.getShipmentCivicNumber(),
                        o.getShipmentCity(),
                        o.getShipmentPostalCode()
                )
        );
        holder.getTotal().setText(Util.formatCurrency(o.getTotal()));
        final OrderProductAdapter adapter = new OrderProductAdapter(getContext());
        holder.getProducts().setAdapter(adapter);
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) getContext())
                .getOrderProducts(o.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        adapter::addAll,
                        this::handleError
                )
        );
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) getContext())
                .getOrderStates(o.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        states -> {
                            if (!states.isEmpty()) {
                                holder.getDate().setText(
                                        getContext().getString(
                                                R.string.order_date,
                                                Util.formatDate(states.get(0).getDate(), Util.DATE_FORMAT)
                                        )
                                );
                                final Order.State state = Order.State.valueOf(states.get(states.size() - 1).getState().toString());
                                holder.getState().setText(
                                        getContext().getString(
                                                R.string.order_state,
                                                getContext().getResources().getStringArray(R.array.order_states)[state.ordinal()]
                                        )
                                );
                            }
                        },
                        this::handleError
                )
        );
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(getContext(), TAG, error);
    }

}
