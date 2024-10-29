package ru.korostelev.basket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.korostelev.basket.entity.Product;
import ru.korostelev.basket.repository.BasketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketServiceImp implements BasketService {

    private final BasketRepository basketRepository;

    @Override
    public List<Product> findProductsInBasket(){
        return this.basketRepository.findAllProducts();
    }

    @Override
    public Product findProductById(int id){
        return this.basketRepository.findProductById(id);
    }

    @Override
    public Product addToBasket(int id, int count) {
        return this.basketRepository.save(new Product(id, count));
    }

    @Override
    public void deleteFromBasketById(int id) {
        this.basketRepository.deleteById(id);
    }
}
