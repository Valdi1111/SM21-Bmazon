package org.valdi.bmazon.network;

import androidx.annotation.NonNull;

import org.valdi.bmazon.model.UserType;

public class LoginResponse {
    private String id;
    private UserType type;
    private String token;

    public String getId() {
        return id;
    }

    public UserType getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginResponse{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", token='" + token + '\'' +
                '}';
    }
}
