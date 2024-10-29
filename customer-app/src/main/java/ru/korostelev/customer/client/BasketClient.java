package ru.korostelev.customer.client;

import ru.korostelev.customer.entity.BasketProduct;

import java.util.List;

public interface BasketClient {

    List<BasketProduct> findProductsInBasket();

    BasketProduct findBasketProductById(int id);

    BasketProduct addProductToBasket(int id, int count);

    void removeProductFromBasket(int id);
}
