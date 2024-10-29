package ru.korostelev.basket.repository;

import ru.korostelev.basket.entity.Product;

import java.util.List;

public interface BasketRepository {

    List<Product> findAllProducts();

    Product findProductById(Integer id);

    Product save(Product product);

    void deleteById(Integer id);
}
