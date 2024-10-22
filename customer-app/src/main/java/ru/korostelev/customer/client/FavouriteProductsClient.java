package ru.korostelev.customer.client;

import ru.korostelev.customer.entity.FavouriteProduct;

import java.util.List;

public interface FavouriteProductsClient {

    List<FavouriteProduct> findFavouriteProducts();

    FavouriteProduct findFavouriteProductByProductId(int id);

    FavouriteProduct addProductToFavourites(int id);

    void removeProductFromFavourites(int id);
}
