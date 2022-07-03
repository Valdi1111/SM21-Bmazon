package org.valdi.bmazon.fragments.notifications;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travijuu.numberpicker.library.NumberPicker;

import org.valdi.bmazon.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final TextView description;
    private final TextView read;
    private final TextView date;

    public NotificationViewHolder(@NonNull View view) {
        super(view);
        this.title = view.findViewById(R.id.notification_title);
        this.description = view.findViewById(R.id.notification_description);
        this.read = view.findViewById(R.id.notification_read);
        this.date = view.findViewById(R.id.notification_date);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDescription() {
        return description;
    }

    public TextView getRead() {
        return read;
    }

    public TextView getDate() {
        return date;
    }
}
