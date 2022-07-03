package org.valdi.bmazon.model.product;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ProductImage {
    @SerializedName("path")
    private String path;
    @SerializedName("order")
    private int order;

    public String getPath() {
        return path;
    }

    public int getOrder() {
        return order;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductImage{" +
                "path='" + path + '\'' +
                ", order=" + order +
                '}';
    }
}
