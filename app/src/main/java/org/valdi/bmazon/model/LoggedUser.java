package org.valdi.bmazon.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class LoggedUser {
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private UserType type;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;
    @SerializedName("fiscal_code")
    private String fiscalCode;
    @SerializedName("gender")
    private Gender gender;

    public int getId() {
        return id;
    }

    public UserType getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggedUser that = (LoggedUser) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "LoggedUser{" +
                "id=" + id +
                ", type=" + type +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", fiscalCode='" + fiscalCode + '\'' +
                ", gender=" + gender +
                '}';
    }

    public enum Gender {
        male,
        female,
        other
    }
}
