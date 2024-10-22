package ru.korostelev.feedback.service;

import ru.korostelev.feedback.entity.FavouriteProduct;

import java.util.List;

public interface FavouriteProductsService {

    FavouriteProduct addProductToFavourites(int id);

    void removeProductFromFavourites(int id);

    FavouriteProduct findFavouriteProductByProduct(int id);

    List<FavouriteProduct> findFavouriteProducts();
}
