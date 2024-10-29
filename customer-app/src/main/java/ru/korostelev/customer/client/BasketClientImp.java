package ru.korostelev.customer.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.korostelev.customer.client.exception.BadRequestException;
import ru.korostelev.customer.client.payload.NewBasketProductPayload;
import ru.korostelev.customer.entity.BasketProduct;
import ru.korostelev.customer.entity.FavouriteProduct;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class BasketClientImp implements BasketClient {

    private static final ParameterizedTypeReference<List<BasketProduct>> BASKET_PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<BasketProduct> findProductsInBasket() {
        return this.restClient
                .get()
                .uri("/basket-api/added-products")
                .retrieve()
                .body(BASKET_PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public BasketProduct findBasketProductById(int id){
        try {
            return this.restClient
                    .get()
                    .uri("/basket-api/added-products/by-product-id/{id}", id)
                    .retrieve()
                    .body(BasketProduct.class);
        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }


    @Override
    public BasketProduct addProductToBasket(int id, int count) {
        try {
            return this.restClient
                    .post()
                    .uri("/basket-api/added-products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewBasketProductPayload(id, count))
                    .retrieve()
                    .body(BasketProduct.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void removeProductFromBasket(int id) {
        try {
            this.restClient
                    .delete()
                    .uri("/basket-api/added-products/by-product-id/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
