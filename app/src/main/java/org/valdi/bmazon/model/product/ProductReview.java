package org.valdi.bmazon.model.product;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ProductReview {
    @SerializedName("id")
    private int id;
    @SerializedName("reviewer_id")
    private int reviewerId;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("username")
    private String username;
    @SerializedName("created")
    private String created;
    @SerializedName("rating")
    private int rating;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("image")
    private String image;
    @SerializedName("upvotes")
    private int upvotes;

    public int getId() {
        return id;
    }

    public int getReviewer_id() {
        return reviewerId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getCreated() {
        return created;
    }

    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public int getUpvotes() {
        return upvotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductReview that = (ProductReview) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + id +
                ", reviewer_id=" + reviewerId +
                ", avatar='" + avatar + '\'' +
                ", username='" + username + '\'' +
                ", created='" + created + '\'' +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", upvotes=" + upvotes +
                '}';
    }

    public enum Order {
        date,
        helpful
    }
}
