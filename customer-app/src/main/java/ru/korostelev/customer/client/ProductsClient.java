package ru.korostelev.customer.client;

import ru.korostelev.customer.entity.Product;

import java.util.List;

public interface ProductsClient {

    List<Product> findAllProducts(String filter);

    Product findProduct(Integer id);

}
