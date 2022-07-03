package org.valdi.bmazon.network;

import androidx.annotation.NonNull;

public class ErrorResponse {
    private String error;
    private String message;

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
