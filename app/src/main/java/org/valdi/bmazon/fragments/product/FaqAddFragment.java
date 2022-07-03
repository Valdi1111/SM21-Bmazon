package org.valdi.bmazon.fragments.product;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import org.valdi.bmazon.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaqAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaqAddFragment extends DialogFragment {
    public static final String TAG = FaqAddFragment.class.getSimpleName();;
    public static final String KEY = "faq_add_key";
    public static final String QUESTION_PARAM = "question";

    private TextInputEditText question;

    public FaqAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FaqAddFragment.
     */
    public static FaqAddFragment newInstance() {
        return new FaqAddFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_faq_add, null, false);
        this.question = view.findViewById(R.id.faq_question);
        if (savedInstanceState != null) {
            this.question.setText(savedInstanceState.getString(QUESTION_PARAM));
        }
        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.faq_add_title)
                .setView(view)
                .setNeutralButton(R.string.action_cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton(R.string.action_done, (dialog, which) -> {
                    final Bundle bundle = new Bundle();
                    bundle.putString(QUESTION_PARAM, question.getText().toString());
                    getParentFragmentManager().setFragmentResult(KEY, bundle);
                })
                .create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUESTION_PARAM, this.question.getText().toString());
    }
}