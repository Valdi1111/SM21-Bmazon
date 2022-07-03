package org.valdi.bmazon.fragments.orders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.model.order.OrderProduct;
import org.valdi.bmazon.utils.Util;

public class OrderProductAdapter extends AbstractRecyclerViewAdapter<OrderProduct, OrderProductViewHolder> {
    private static final String TAG = OrderProductAdapter.class.getSimpleName();

    public OrderProductAdapter(final Context context) {
        super(context, R.layout.order_product);
    }

    @NonNull
    @Override
    protected OrderProductViewHolder createFromView(@NonNull View view) {
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        final OrderProduct p = getItems().get(position);
        holder.getTitle().setText(p.getTitle());
        holder.getPrice().setText(Util.formatCurrency(p.getPrice()));
        holder.getAmount().setText(
                getContext().getString(
                        R.string.order_amount,
                        p.getAmount()
                )
        );
    }

}
