package org.valdi.bmazon.fragments.cart;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.AbstractRecyclerViewAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.CartProduct;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CartProductAdapter extends AbstractRecyclerViewAdapter<CartProduct, CartProductViewHolder> {
    private static final String TAG = CartProductAdapter.class.getSimpleName();
    private final CompositeSubscription subscriptions;
    private final Runnable updateTotal;
    //private final Runnable updateToolbar;
    //private int selected  = -1;

    public CartProductAdapter(final Context context, final CompositeSubscription subscriptions, final Runnable updateTotal, final Runnable updateToolbar) {
        super(context, R.layout.cart_product);
        this.subscriptions = subscriptions;
        this.updateTotal = updateTotal;
        //this.updateToolbar = updateToolbar;
    }

    @NonNull
    @Override
    protected CartProductViewHolder createFromView(@NonNull View view) {
        return new CartProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductViewHolder holder, int position) {
        //holder.itemView.setOnLongClickListener(v -> {
        //    selected = holder.getAdapterPosition();
        //    holder.itemView.setActivated(true);
        //    updateToolbar.run();
        //    return false;
        //});
        final CartProduct p = getItems().get(position);
        holder.getTitle().setText(p.getTitle());
        Util.formatPriceField(p.getPrice(), p.getDiscount(), holder.getPrice());
        holder.getAmount().setValue(p.getAmount());
        holder.getAmount().setMax(p.getMaxAmount());
        holder.getAmount().setValueChangedListener((value, action) -> {
            final JsonObject request = new JsonObject();
            request.addProperty("product", p.getId());
            request.addProperty("amount", value);
            this.subscriptions.add(NetworkUtil
                    .getRetrofit((AuthProvider) getContext())
                    .putCart(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            unused -> {
                                Toast.makeText(getContext(), R.string.cart_updated_product, Toast.LENGTH_SHORT).show();
                                p.setAmount(value);
                                this.updateTotal.run();
                            },
                            this::handleError
                    )
            );
        });
        holder.getDelete().setOnClickListener(view -> {
            this.subscriptions.add(NetworkUtil
                    .getRetrofit((AuthProvider) getContext())
                    .deleteCart(p.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            unused -> {
                                Toast.makeText(getContext(), R.string.cart_removed_product, Toast.LENGTH_SHORT).show();
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

    @Override
    public void addAll(List<CartProduct> items) {
        super.addAll(items);
        this.updateTotal.run();
    }

    @Override
    public void delete(int position) {
        super.delete(position);
        this.updateTotal.run();
    }

    //public boolean isSelected() {
    //    return selected != -1;
    //}

    //public int deselect() {
    //    return selected = -1;
    //}
}
