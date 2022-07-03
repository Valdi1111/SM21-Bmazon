package org.valdi.bmazon.model.product;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ProductRating {
    @SerializedName("rating")
    private int rating;
    @SerializedName("amount")
    private int amount;

    public int getRating() {
        return rating;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductRating that = (ProductRating) o;
        return rating == that.rating;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductRating{" +
                "rating=" + rating +
                ", amount=" + amount +
                '}';
    }
}
