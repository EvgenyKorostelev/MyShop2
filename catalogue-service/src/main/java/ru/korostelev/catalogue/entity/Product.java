package ru.korostelev.catalogue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "catalogue", name = "t_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_title")
//    @NotNull
//    @Size(min = 3, max = 50)
    private String title;

    @Column(name = "c_description")
//    @Size(max = 1000)
    private String description;

    public Product(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
