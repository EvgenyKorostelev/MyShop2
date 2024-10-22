package ru.korostelev.customer.client;


import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.korostelev.customer.entity.Product;

import java.util.List;


@RequiredArgsConstructor
public class ProductsClientImp implements ProductsClient {

    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String filter) {
        return this.restClient
                .get()
                .uri("/catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public Product findProduct(Integer id) {
        try {
            return this.restClient
                    .get()
                    .uri("/catalogue-api/products/{id}", id)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }
}

