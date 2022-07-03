package org.valdi.bmazon.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Payment {
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private String type;
    @SerializedName("number")
    private String number;
    @SerializedName("owner")
    private String owner;
    @SerializedName("expire")
    private String expire;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    public String getExpire() {
        return expire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id == payment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", number='" + number + '\'' +
                ", owner='" + owner + '\'' +
                ", expire='" + expire + '\'' +
                '}';
    }

    public String prettyString() {
        return type +
                " ****" +
                number.substring(12, 15);
    }
}
