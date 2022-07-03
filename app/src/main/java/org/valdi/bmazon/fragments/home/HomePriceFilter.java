package org.valdi.bmazon.fragments.home;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.valdi.bmazon.R;
import org.valdi.bmazon.utils.Util;

public class HomePriceFilter extends DialogFragment {
    public static final String TAG = HomePriceFilter.class.getSimpleName();
    public static final String KEY = "price_filter_key";
    public static final String MIN_PRICE_PARAM = "min_price";
    public static final String MAX_PRICE_PARAM = "max_price";
    private TextInputEditText minPrice;
    private TextInputEditText maxPrice;

    public HomePriceFilter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param minPrice current min price
     * @param maxPrice current max price
     * @return A new instance of fragment HomeCategoryFilter.
     */
    public static HomePriceFilter newInstance(final Double minPrice, final Double maxPrice) {
        final HomePriceFilter fragment = new HomePriceFilter();
        final Bundle args = new Bundle();
        args.putDouble(MIN_PRICE_PARAM, minPrice);
        args.putDouble(MAX_PRICE_PARAM, maxPrice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_price_filter, (ViewGroup) getView(), false);
        this.minPrice = view.findViewById(R.id.filter_min_price);
        this.maxPrice = view.findViewById(R.id.filter_max_price);
        if (savedInstanceState != null) {
            this.minPrice.setText(savedInstanceState.getString(MIN_PRICE_PARAM));
            this.maxPrice.setText(savedInstanceState.getString(MAX_PRICE_PARAM));
        } else if (getArguments() != null) {
            double min = getArguments().getDouble(MIN_PRICE_PARAM, -1);
            double max = getArguments().getDouble(MAX_PRICE_PARAM, -1);
            this.minPrice.setText(min == -1 ? "" : String.valueOf(min));
            this.maxPrice.setText(max == -1 ? "" : String.valueOf(max));
        }
        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.choose_price))
                .setView(view)
                .setNeutralButton(getString(R.string.action_cancel), (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton(getString(R.string.action_done), (dialog, which) -> {
                    final Bundle bundle = new Bundle();
                    bundle.putDouble(MIN_PRICE_PARAM, Util.getPositiveDoubleOr(minPrice.getText().toString(), -1));
                    bundle.putDouble(MAX_PRICE_PARAM, Util.getPositiveDoubleOr(maxPrice.getText().toString(), -1));
                    getParentFragmentManager().setFragmentResult(KEY, bundle);
                })
                .create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MIN_PRICE_PARAM, this.minPrice.getText().toString());
        outState.putString(MAX_PRICE_PARAM, this.maxPrice.getText().toString());
    }
}
