package ru.korostelev.customer.service;

import ru.korostelev.customer.entity.FavouriteProduct;

import java.util.List;

public interface FavouriteProductsService {

    FavouriteProduct addProductToFavourites(int id);

    Void removeProductFromFavourites(int id);

    FavouriteProduct findFavouriteProductByProduct(int id);

    List<FavouriteProduct> findFavouriteProducts();
}
