package org.valdi.bmazon.model.order;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class OrderState {
    @SerializedName("state")
    private Order.State state;
    @SerializedName("date")
    private String date;

    public Order.State getState() {
        return state;
    }

    public String getDate() {
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return "OrderState{" +
                "state=" + state +
                ", date='" + date + '\'' +
                '}';
    }
}
