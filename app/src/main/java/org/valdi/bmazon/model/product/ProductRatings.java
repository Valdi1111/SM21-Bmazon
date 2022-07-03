package org.valdi.bmazon.model.product;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductRatings {
    @SerializedName("average")
    private double average;
    @SerializedName("amount")
    private int amount;
    @SerializedName("ratings")
    private List<ProductRating> ratings;

    public double getAverage() {
        return average;
    }

    public int getAmount() {
        return amount;
    }

    public List<ProductRating> getRatings() {
        return ratings;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductRatings{" +
                "average=" + average +
                ", amount=" + amount +
                ", ratings=" + ratings +
                '}';
    }
}
