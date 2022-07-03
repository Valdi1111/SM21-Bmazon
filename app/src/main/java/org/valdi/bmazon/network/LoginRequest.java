package org.valdi.bmazon.network;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(final String email, final String password) {
        this.email = email;
        this.password = password;
    }
}
