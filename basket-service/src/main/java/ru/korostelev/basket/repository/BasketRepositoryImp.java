package ru.korostelev.basket.repository;

import org.springframework.stereotype.Repository;
import ru.korostelev.basket.entity.Product;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class BasketRepositoryImp implements BasketRepository {

    private final List<Product> products = Collections.synchronizedList(new LinkedList<>());

    @Override
    public List<Product> findAllProducts() {
        return products;
    }

    @Override
    public Product findProductById(Integer id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public Product save(Product product) {
        this.products.add(product);
        return product;
    }

    @Override
    public void deleteById(Integer id) {
        this.products.forEach(product -> {
            if (product.getId().equals(id)) {
                products.remove(product);
            }
        });
    }
}
