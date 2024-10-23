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
@Table(schema = "feedback", name = "t_favourite")
public class FavouriteProduct {

    @Id
    @Column(name = "id")
    private UUID uuid;

    @Column(name = "c_productid")
    private int id;
}
