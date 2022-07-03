package org.valdi.bmazon.fragments.product;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.valdi.bmazon.R;

public class FaqViewHolder extends RecyclerView.ViewHolder {
    private final int color;
    private final ImageView voteUp;
    private final ImageView voteDown;
    private final TextView votes;
    private final TextView question;
    private final TextView answer;
    private int value = 0;

    public FaqViewHolder(@NonNull View view) {
        super(view);
        this.color = view.getContext().getColor(R.color.faq_vote_activated);
        this.voteUp = view.findViewById(R.id.faq_vote_up);
        this.voteDown = view.findViewById(R.id.faq_vote_down);
        this.votes = view.findViewById(R.id.faq_votes);
        this.question = view.findViewById(R.id.faq_question);
        this.answer = view.findViewById(R.id.faq_answer);
    }

    public ImageView getVoteUp() {
        return voteUp;
    }

    public ImageView getVoteDown() {
        return voteDown;
    }

    public TextView getVotes() {
        return votes;
    }

    public TextView getQuestion() {
        return question;
    }

    public TextView getAnswer() {
        return answer;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int val) {
        this.value = val;
        changeButton(voteUp, val == 1);
        changeButton(voteDown, val == -1);
    }

    private void changeButton(final ImageView view, boolean value) {
        if(value) {
            view.setColorFilter(this.color);
        } else {
            view.clearColorFilter();
        }
    }
}
