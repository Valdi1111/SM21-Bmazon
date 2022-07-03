package org.valdi.bmazon.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final Context context;
    private final int layout;
    private final List<T> items;

    public AbstractRecyclerViewAdapter(final Context context, @LayoutRes final int layout) {
        this.context = context;
        this.layout = layout;
        this.items = new ArrayList<>();
    }

    protected Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(this.layout, parent, false);
        return createFromView(view);
    }

    @NonNull
    protected abstract VH createFromView(@NonNull View view);

    protected List<T> getItems() {
        return this.items;
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    /**
     * Add new items to view and update.
     * @param items the items
     */
    public void addAll(final List<T> items) {
        int size = getItems().size();
        if (!getItems().addAll(items)) {
            return;
        }
        notifyItemRangeInserted(size, getItems().size() - size);
    }

    /**
     * Remove an item from view and update.
     * @param position item position
     */
    public void delete(final int position) {
        getItems().remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Remove all items from view and update.
     */
    public void clear() {
        int size = getItems().size();
        if (size == 0) {
            return;
        }
        getItems().clear();
        notifyItemRangeRemoved(0, size);
    }

}
