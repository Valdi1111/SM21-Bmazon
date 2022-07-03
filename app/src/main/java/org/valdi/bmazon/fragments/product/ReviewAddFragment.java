package org.valdi.bmazon.fragments.product;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.View;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.valdi.bmazon.R;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewAddFragment extends DialogFragment {
    public static final String TAG = ReviewAddFragment.class.getSimpleName();;
    public static final String KEY = "review_add_key";
    public static final String TITLE_PARAM = "question";
    public static final String DESCRIPTION_PARAM = "description";
    public static final String RATING_PARAM = "rating";

    private TextInputEditText title;
    private TextInputEditText description;

    public ReviewAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewAddFragment.
     */
    public static ReviewAddFragment newInstance() {
        return new ReviewAddFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_review_add, null, false);
        this.title = view.findViewById(R.id.review_title);
        this.description = view.findViewById(R.id.review_description);
        final MaterialAutoCompleteTextView rating = view.findViewById(R.id.review_rating);
        if (savedInstanceState != null) {
            this.title.setText(savedInstanceState.getString(TITLE_PARAM));
            this.description.setText(savedInstanceState.getString(DESCRIPTION_PARAM));
        }
        // TODO add image
        //final Button image = view.findViewById(R.id.review_image);
        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.review_add_title)
                .setView(view)
                .setNeutralButton(R.string.action_cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton(R.string.action_done, (dialog, which) -> {
                    final String r = rating.getText().toString();
                    final String[] ratings = getResources().getStringArray(R.array.review_ratings);
                    final Bundle bundle = new Bundle();
                    bundle.putString(TITLE_PARAM, title.getText().toString());
                    bundle.putString(DESCRIPTION_PARAM, description.getText().toString());
                    bundle.putInt(RATING_PARAM, Arrays.asList(ratings).indexOf(r) + 1);
                    getParentFragmentManager().setFragmentResult(KEY, bundle);
                })
                .create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE_PARAM, this.title.getText().toString());
        outState.putString(DESCRIPTION_PARAM, this.description.getText().toString());
    }
}