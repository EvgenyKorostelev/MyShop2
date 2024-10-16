package ru.korostelev.customer.repository;

import org.springframework.stereotype.Repository;
import ru.korostelev.customer.entity.FavouriteProduct;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class FavouriteProductRepositoryImp implements FavouriteProductRepository{

    private final List<FavouriteProduct> favouriteProducts = Collections.synchronizedList(new LinkedList<>());

    @Override
    public FavouriteProduct save(FavouriteProduct favouriteProduct) {
        this.favouriteProducts.add(favouriteProduct);
        return Mono.just(favouriteProduct);
    }

    @Override
    public Void deleteByProductId(int id) {
        this.favouriteProducts.removeIf(favouriteProduct -> favouriteProduct.getProductId() == id);
        return Mono.empty();
    }

    @Override
    public FavouriteProduct findByProductId(int id) {
        return Flux.fromIterable(this.favouriteProducts)
                .filter(favouriteProduct -> favouriteProduct.getProductId() == id)
                .singleOrEmpty();
    }

    @Override
    public List<FavouriteProduct> findAll() {
        return Flux.fromIterable(this.favouriteProducts);
    }
}
