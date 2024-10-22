package ru.korostelev.feedback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "feedback", name = "t_review")
public class ProductReview {

    @Id
    private UUID uuid;

    @Column(name = "c_productid")
    private int id;

    @Column(name = "c_rating")
    private int rating;

    @Column(name = "c_review")
    private String review;
}
