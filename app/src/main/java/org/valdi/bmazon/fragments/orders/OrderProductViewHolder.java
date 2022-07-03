package org.valdi.bmazon.fragments.orders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.valdi.bmazon.R;

public class OrderProductViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final TextView price;
    private final TextView amount;

    public OrderProductViewHolder(@NonNull View view) {
        super(view);
        this.title = view.findViewById(R.id.product_title);
        this.price = view.findViewById(R.id.product_price);
        this.amount = view.findViewById(R.id.product_amount);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getPrice() {
        return price;
    }

    public TextView getAmount() {
        return amount;
    }
}
