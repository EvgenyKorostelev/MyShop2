package ru.korostelev.customer.repository;

import ru.korostelev.customer.entity.FavouriteProduct;

import java.util.List;

public interface FavouriteProductRepository {

    FavouriteProduct save(FavouriteProduct favouriteProduct);

    Void deleteByProductId(int id);

    FavouriteProduct findByProductId(int id);

    List<FavouriteProduct> findAll();
}
