package ru.korostelev.feedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korostelev.feedback.entity.FavouriteProduct;
import ru.korostelev.feedback.repository.FavouriteProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteProductsServiceImp implements FavouriteProductsService {

    private final FavouriteProductRepository favouriteProductRepository;

    @Override
    @Transactional
    public FavouriteProduct addProductToFavourites(int id) {
        return this.favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), id));
    }

    @Override
    @Transactional
    public void removeProductFromFavourites(int id) {
        this.favouriteProductRepository.deleteById(id);
    }

    @Override
    public FavouriteProduct findFavouriteProductByProduct(int id) {
        return this.favouriteProductRepository.findById(id);
    }

    @Override
    public List<FavouriteProduct> findFavouriteProducts() {
        return this.favouriteProductRepository.findAll();
    }
}
