package ru.korostelev.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korostelev.feedback.entity.FavouriteProduct;

import java.util.UUID;

public interface FavouriteProductRepository extends JpaRepository<FavouriteProduct, UUID> {

    void deleteById(int id);

    FavouriteProduct findById(int id);
}
