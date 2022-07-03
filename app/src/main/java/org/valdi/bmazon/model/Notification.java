package org.valdi.bmazon.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private String type;
    @SerializedName("data")
    private String data;
    @SerializedName("created")
    private String created;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getCreated() {
        return created;
    }
}
