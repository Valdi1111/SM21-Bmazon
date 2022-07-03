package org.valdi.bmazon.fragments.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.valdi.bmazon.R;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {
    private final ImageView image;
    private final TextView title;
    private final TextView description;
    private final TextView price;
    private final TextView currentPrice;

    public ProductCardViewHolder(@NonNull View view) {
        super(view);
        this.image = view.findViewById(R.id.product_image);
        this.title = view.findViewById(R.id.product_title);
        this.description = view.findViewById(R.id.product_description);
        this.price = view.findViewById(R.id.product_price);
        this.currentPrice = view.findViewById(R.id.product_current_price);
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDescription() {
        return description;
    }

    public TextView getPrice() {
        return price;
    }

    public TextView getCurrentPrice() {
        return currentPrice;
    }
}
