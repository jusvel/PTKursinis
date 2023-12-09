package com.kursinis.ptkursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoolReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private Double rating;
    private String comment;
    private boolean isReview;
    private Integer parentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    public String getReviewText() {
        return user.username + ": " + rating + "/5.0" + " - " + comment;
    }
    public String getReplyText() {
        return comment + "\nBy: " + user.getUsername();
    }

    @Override
    public String toString() {
        if (parentId == null) {
            return getReviewText();
        } else {
            return getReplyText();
        }
    }
}
