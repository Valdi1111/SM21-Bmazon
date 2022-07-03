package org.valdi.bmazon.fragments.orders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.valdi.bmazon.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    private final TextView date;
    private final TextView state;
    private final TextView shipment;
    private final RecyclerView products;
    private final TextView total;

    public OrderViewHolder(@NonNull View view) {
        super(view);
        this.date = view.findViewById(R.id.order_date);
        this.state = view.findViewById(R.id.order_state);
        this.shipment = view.findViewById(R.id.order_shipment);
        this.products = view.findViewById(R.id.products_list);
        this.total = view.findViewById(R.id.order_total);
    }

    public TextView getDate() {
        return date;
    }

    public TextView getState() {
        return state;
    }

    public TextView getShipment() {
        return shipment;
    }

    public RecyclerView getProducts() {
        return products;
    }

    public TextView getTotal() {
        return total;
    }
}
