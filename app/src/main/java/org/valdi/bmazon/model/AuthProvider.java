package org.valdi.bmazon.model;

public interface AuthProvider {

    boolean isLogged();

    String getToken();

}
