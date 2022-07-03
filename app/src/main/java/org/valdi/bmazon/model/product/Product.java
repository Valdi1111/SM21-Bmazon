package org.valdi.bmazon.model.product;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Product {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("price")
    private double price;
    @SerializedName("discount")
    private double discount;
    @SerializedName("amount")
    private int amount;
    @SerializedName("seller_id")
    private int sellerId;
    @SerializedName("business_name")
    private String businessName;
    @SerializedName("visible")
    private int visible;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public int getSellerId() {
        return sellerId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public boolean isVisible() {
        return visible == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", amount=" + amount +
                ", seller_id=" + sellerId +
                ", business_name='" + businessName + '\'' +
                ", visible=" + visible +
                '}';
    }
}
