package org.valdi.bmazon.fragments.home;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.fragments.NoConnectionFragment;
import org.valdi.bmazon.fragments.ProductFragment;
import org.valdi.bmazon.model.ShopProduct;
import org.valdi.bmazon.utils.Util;

public class ProductCardAdapter extends AbstractRecyclerViewAdapter<ShopProduct, ProductCardViewHolder> {
    private final FragmentManager manager;

    public ProductCardAdapter(final Activity activity, final FragmentManager manager) {
        super(activity, R.layout.product_card);
        this.manager = manager;
    }

    @NonNull
    @Override
    protected ProductCardViewHolder createFromView(@NonNull View view) {
        return new ProductCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, int position) {
        final ShopProduct p = getItems().get(position);
        // TODO remove default product image
        Util.setProductImage(holder.getImage(), p.getCover());
        //Util.setProductImage(holder.getImage(), "50b197dcba2ac28aef23c9406235a69f");
        holder.getTitle().setText(p.getTitle());
        holder.getDescription().setText(p.getDescription());
        Util.formatPriceFields(p.getPrice(), p.getDiscount(), holder.getPrice(), holder.getCurrentPrice());
        holder.itemView.setOnClickListener(view -> {
            final FragmentTransaction transaction = this.manager.beginTransaction();
            if (Util.isNetworkConnected((Activity) getContext())) {
                transaction.replace(R.id.main_fragment_container, ProductFragment.newInstance(p.getId()));
            } else {
                final NoConnectionFragment noConnectionFragment = NoConnectionFragment.newInstance(() -> {
                    final FragmentTransaction t = this.manager.beginTransaction();
                    t.addToBackStack("no-conn");
                    t.replace(R.id.main_fragment_container, ProductFragment.newInstance(p.getId()));
                    t.commit();
                });
                transaction.replace(R.id.main_fragment_container, noConnectionFragment);
            }
            transaction.addToBackStack("product");
            transaction.commit();
        });
    }

}
