package ru.korostelev.basket.service;

import ru.korostelev.basket.entity.Product;

import java.util.List;

public interface BasketService {

    List<Product> findProductsInBasket();

    Product findProductById(int id);

    Product addToBasket(int id, int count);

    void deleteFromBasketById(int id);
}
