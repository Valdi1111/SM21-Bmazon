package org.valdi.bmazon.model;

import com.google.gson.annotations.SerializedName;

public class BuyerData {
    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;
    @SerializedName("fiscal_code")
    private String fiscalCode;
    @SerializedName("gender")
    private LoggedUser.Gender gender;

    public BuyerData setName(String name) {
        this.name = name;
        return this;
    }

    public BuyerData setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public BuyerData setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
        return this;
    }

    public BuyerData setGender(LoggedUser.Gender gender) {
        this.gender = gender;
        return this;
    }
}
