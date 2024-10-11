package ru.korostelev.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korostelev.catalogue.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Integer> {
}
