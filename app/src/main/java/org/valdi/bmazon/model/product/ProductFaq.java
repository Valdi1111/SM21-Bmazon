package org.valdi.bmazon.model.product;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ProductFaq {
    @SerializedName("id")
    private int id;
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private String answer;
    @SerializedName("created")
    private String created;
    @SerializedName("upvotes")
    private int upvotes;

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCreated() {
        return created;
    }

    public int getUpvotes() {
        return upvotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductFaq that = (ProductFaq) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductFaq{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", created='" + created + '\'' +
                ", upvotes=" + upvotes +
                '}';
    }
}
