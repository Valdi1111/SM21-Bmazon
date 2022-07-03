package org.valdi.bmazon.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CartProduct {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("max_amount")
    private int maxAmount;
    @SerializedName("price")
    private double price;
    @SerializedName("discount")
    private double discount;
    @SerializedName("amount")
    private int amount;
    @SerializedName("cover")
    private String cover;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int value) {
        this.amount = value;
    }

    public String getCover() {
        return cover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProduct that = (CartProduct) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "CartProduct{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", max_amount=" + maxAmount +
                ", price=" + price +
                ", discount=" + discount +
                ", amount=" + amount +
                ", cover='" + cover + '\'' +
                '}';
    }
}
