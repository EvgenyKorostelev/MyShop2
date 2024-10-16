package ru.korostelev.catalogue.service;

import ru.korostelev.catalogue.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAllProducts(String filter);

    Product createProduct(String productName, String description);

    Optional<Product> findProductById(Integer id);

    void updateProduct(Integer id, String title, String description);

    void deleteProductById(Integer id);

}
