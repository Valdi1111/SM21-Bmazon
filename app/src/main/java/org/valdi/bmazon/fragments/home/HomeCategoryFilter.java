package org.valdi.bmazon.fragments.home;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.valdi.bmazon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeCategoryFilter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeCategoryFilter extends DialogFragment {
    public static final String TAG = HomeCategoryFilter.class.getSimpleName();
    public static final String KEY = "category_filter_key";
    public static final String CATEGORIES_PARAM = "categories";
    public static final String CATEGORY_PARAM = "category";
    private List<String> categories = new ArrayList<>();
    private int category = 0;

    public HomeCategoryFilter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param categories list of available categories
     * @param category current category
     * @return A new instance of fragment HomeCategoryFilter.
     */
    public static HomeCategoryFilter newInstance(final List<String> categories, final int category) {
        final HomeCategoryFilter fragment = new HomeCategoryFilter();
        final Bundle args = new Bundle();
        args.putStringArrayList(CATEGORIES_PARAM, new ArrayList<>(categories));
        args.putInt(CATEGORY_PARAM, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.categories = getArguments().getStringArrayList(CATEGORIES_PARAM);
            this.category = getArguments().getInt(CATEGORY_PARAM);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.choose_category))
                .setSingleChoiceItems(
                        categories.toArray(new String[0]),
                        category,
                        (dialog, which) -> {
                            category = which;
                        }
                )
                .setNeutralButton(getString(R.string.action_cancel), (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton(getString(R.string.action_done), (dialog, which) -> {
                    final Bundle bundle = new Bundle();
                    bundle.putInt(CATEGORY_PARAM, category);
                    getParentFragmentManager().setFragmentResult(KEY, bundle);
                })
                .create();
    }
}