package ru.korostelev.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korostelev.catalogue.entity.Product;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByTitleLikeIgnoreCase(String filter);
}
