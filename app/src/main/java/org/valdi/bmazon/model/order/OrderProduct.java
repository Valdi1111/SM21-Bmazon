package org.valdi.bmazon.model.order;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class OrderProduct {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("amount")
    private int amount;
    @SerializedName("price")
    private double price;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProduct that = (OrderProduct) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }
}
