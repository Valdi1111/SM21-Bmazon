package org.valdi.bmazon.model.order;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Order {
    @SerializedName("id")
    private int id;
    @SerializedName("total")
    private double total;
    @SerializedName("payment_type")
    private String paymentType;
    @SerializedName("payment_data")
    private String paymentData;
    @SerializedName("shipment_id")
    private int shipmentId;
    @SerializedName("shipment_street")
    private String shipmentStreet;
    @SerializedName("shipment_civic_number")
    private String shipmentCivicNumber;
    @SerializedName("shipment_postal_code")
    private String shipmentPostalCode;
    @SerializedName("shipment_city")
    private String shipmentCity;
    @SerializedName("shipment_district")
    private String shipmentDistrict;

    public int getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentData() {
        return paymentData;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public String getShipmentStreet() {
        return shipmentStreet;
    }

    public String getShipmentCivicNumber() {
        return shipmentCivicNumber;
    }

    public String getShipmentPostalCode() {
        return shipmentPostalCode;
    }

    public String getShipmentCity() {
        return shipmentCity;
    }

    public String getShipmentDistrict() {
        return shipmentDistrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", total=" + total +
                ", paymentType='" + paymentType + '\'' +
                ", paymentData='" + paymentData + '\'' +
                ", shipmentId=" + shipmentId +
                ", shipmentStreet='" + shipmentStreet + '\'' +
                ", shipmentCivicNumber='" + shipmentCivicNumber + '\'' +
                ", shipmentPostalCode='" + shipmentPostalCode + '\'' +
                ", shipmentCity='" + shipmentCity + '\'' +
                ", shipmentDistrict='" + shipmentDistrict + '\'' +
                '}';
    }

    public enum State {
        created,
        accepted,
        shipped,
        received
    }

}
