package org.valdi.bmazon.fragments.cart;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travijuu.numberpicker.library.NumberPicker;

import org.valdi.bmazon.R;

public class CartProductViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final TextView price;
    private final NumberPicker amount;
    private final ImageView delete;

    public CartProductViewHolder(@NonNull View view) {
        super(view);
        this.title = view.findViewById(R.id.product_title);
        this.price = view.findViewById(R.id.product_price);
        this.amount = view.findViewById(R.id.product_amount);
        this.delete = view.findViewById(R.id.product_delete);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getPrice() {
        return price;
    }

    public NumberPicker getAmount() {
        return amount;
    }

    public ImageView getDelete() {
        return delete;
    }
}
