package ru.korostelev.catalogue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korostelev.catalogue.entity.Product;
import ru.korostelev.catalogue.repository.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;


    @Override
    public List<Product> findAllProducts(String filter) {
        if(filter != null && !filter.isBlank()){
            return this.productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
        } else {
            return this.productRepository.findAll();
        }
    }

    @Override
    @Transactional
    public Product createProduct(String productName, String description) {
        return this.productRepository.save(new Product(null, productName, description));
    }

    @Override
    public Optional<Product> findProductById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateProduct(Integer id, String title, String description) {
        this.productRepository.findById(id)
                .ifPresentOrElse(product -> {
                    product.setTitle(title);
                    product.setDescription(description);
                }, () -> {
                    throw new NoSuchElementException();
                });

    }

    @Override
    @Transactional
    public void deleteProductById(Integer id) {
        this.productRepository.deleteById(id);
    }
}
