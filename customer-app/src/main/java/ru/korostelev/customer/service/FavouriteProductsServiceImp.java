package ru.korostelev.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.korostelev.customer.entity.FavouriteProduct;
import ru.korostelev.customer.repository.FavouriteProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteProductsServiceImp implements FavouriteProductsService {

    private final FavouriteProductRepository favouriteProductRepository;

    @Override
    public FavouriteProduct addProductToFavourites(int id) {
        return this.favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), id));
    }

    @Override
    public Void removeProductFromFavourites(int id) {
        return this.favouriteProductRepository.deleteByProductId(id);
    }

    @Override
    public FavouriteProduct findFavouriteProductByProduct(int id) {
        return this.favouriteProductRepository.findByProductId(id);
    }

    @Override
    public List<FavouriteProduct> findFavouriteProducts() {
        return this.favouriteProductRepository.findAll();
    }
}
