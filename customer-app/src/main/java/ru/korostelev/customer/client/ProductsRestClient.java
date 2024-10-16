package ru.korostelev.customer.client;

import ru.korostelev.customer.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRestClient {

    List<Product> findAllProducts(String filter);

    Optional<Product> findProduct(Integer id);

}
