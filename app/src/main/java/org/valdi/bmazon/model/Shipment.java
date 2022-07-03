package org.valdi.bmazon.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Shipment {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("street")
    private String street;
    @SerializedName("civic_number")
    private String civicNumber;
    @SerializedName("postal_code")
    private String portalCode;
    @SerializedName("city")
    private String city;
    @SerializedName("district")
    private String district;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getCivicNumber() {
        return civicNumber;
    }

    public String getPortalCode() {
        return portalCode;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return id == shipment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Shipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", civicNumber='" + civicNumber + '\'' +
                ", portalCode='" + portalCode + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                '}';
    }

    public String prettyString() {
        return street +
                ", " +
                civicNumber +
                " - " +
                city +
                ", " +
                district +
                " " +
                portalCode;
    }
}
